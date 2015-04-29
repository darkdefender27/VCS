package com.diff.core;

import java.util.*;

import logger.VCSLogger;

public class Diff {
	
	int unchanged=0;
	int modifiedLeft=0;
	int modifiedRight=0;
	int noMatchLeft=0;
	int noMatchRight=0;
	int totalModifications;

	public FileDiffResult diff(String leftFileContent, String rightFileContent, Cancellable cancellable, boolean ignoreLeadingSpaces) {
		ParsedFile leftFile = new ParsedFile(leftFileContent);
		ParsedFile rightFile = new ParsedFile(rightFileContent);
		FileDiffResult result = MyersDiff.diff(leftFile,rightFile,cancellable,ignoreLeadingSpaces);
		//viewResults(result,leftFile,rightFile);
		
		countLinesUnchangedModified(result);
		int deletions,insertions;
		deletions=modifiedLeft+noMatchLeft;
		insertions=modifiedRight+noMatchRight;
		DiffLineResult lineResult=new DiffLineResult();
		lineResult.setNoOfLinesAdded(insertions);
		lineResult.setNoOfLinesDeleted(deletions);
		result.setLineResult(lineResult);
		return result;
	}
	
	private void countLinesUnchangedModified(FileDiffResult result)
	{
		int i=0;
		int leftCount,rightCount;
		if(result!=null)
		{
			leftCount=result.getLeftFile().getLines().length;
			rightCount=result.getRightFile().getLines().length;
			totalModifications=Math.abs(leftCount-rightCount);
			int max=Math.max(leftCount, rightCount);
			while(i<max)
			{
				if(i<leftCount)
				{
					if(result.getLeftFile().getLines()[i].getStatus()==FileLine.MODIFIED)
					{
						modifiedLeft++;
					}
					if(result.getLeftFile().getLines()[i].getStatus()==FileLine.UNCHANGED)
					{
						unchanged++;
					}
					if(result.getLeftFile().getLines()[i].getStatus()==FileLine.NO_MATCH)
					{
						noMatchLeft++;
					}
				}
				if(i<rightCount)
				{
					if(result.getRightFile().getLines()[i].getStatus()==FileLine.MODIFIED)
					{
						modifiedRight++;
					}
					if(result.getRightFile().getLines()[i].getStatus()==FileLine.NO_MATCH)
					{
						noMatchRight++;
					}
				}
				i++;
			}
		}
	}
	
	
	public void viewResults(FileDiffResult result,ParsedFile leftFile,ParsedFile rightFile)
	{
		int i=0,max;
		max=Math.max(result.getLeftFile().getLines().length,result.getRightFile().getLines().length);
		while(i<max)
		{
			if(i<result.getLeftFile().getLines().length)
			{
				if(result.getLeftFile().getLines()[i].getStatus()==FileLine.NO_MATCH  || result.getLeftFile().getLines()[i].getStatus()==FileLine.MODIFIED)
				{
					//deleted
					VCSLogger.infoLogToCmd("deleted     "+result.getLeftFile().getLines()[i].getContent());
				}
				
			}
			if(i<result.getRightFile().getLines().length)
			{
				if(result.getRightFile().getLines()[i].getStatus()==FileLine.NO_MATCH || result.getRightFile().getLines()[i].getStatus()==FileLine.MODIFIED)
				{
					//inserted
					VCSLogger.infoLogToCmd("inserted     "+result.getRightFile().getLines()[i].getContent());
				}
				
			}
			i++;
		}
	}
	
	public static FileDiffResult quickDiff(String leftFileContent, String rightFileContent, boolean ignoreLeadingSpaces) {
		ParsedFile leftFile = new ParsedFile(leftFileContent);
		ParsedFile rightFile = new ParsedFile(rightFileContent);
		FileDiffResult result = new FileDiffResult(leftFile,rightFile);
		lookForMatch(leftFile, rightFile, ignoreLeadingSpaces);
		lookForMatch(rightFile, leftFile, ignoreLeadingSpaces);
		FileLine leftLines[] = leftFile.getLines();
		FileLine rightLines[] = rightFile.getLines();
		int leftIndex = 0;
		int rightIndex = 0;
		while(leftIndex<leftLines.length && rightIndex<rightLines.length)
		{
			// consume identical lines
			while(leftIndex<leftLines.length &&
					rightIndex<rightLines.length &&
					(leftLines[leftIndex].matches(rightLines[rightIndex],ignoreLeadingSpaces)))
			{
				leftLines[leftIndex].setStatus(FileLine.UNCHANGED);
				rightLines[rightIndex].setStatus(FileLine.UNCHANGED);
				leftIndex++;
				rightIndex++;
			}
			// now find the lowest index on both sides where matching starts again. If both indexes
			// have increased, we can consider the skipped blocks on both sides as MODIFIED, otherwise,
			// the bloc for the increased index is NO_MATCH
			int matchLeftIndex = leftLines.length;
			int matchRightIndex = rightLines.length;

			// instead of minimizing the matching bloc, we look for the biggest matching bloc.
			int biggestMatchSize = 0;
			int searchLimit = 15;
			for(int i=leftIndex;biggestMatchSize<searchLimit && i<leftLines.length;i++)
			{
				int matchingIndex = leftLines[i].indexInOtherVersion();
				if ((matchingIndex>-1)&&(matchingIndex >= rightIndex))
				{
					int matchSize = getContiguousMatchingLines(leftLines, rightLines, i, matchingIndex, searchLimit, ignoreLeadingSpaces);
					if (biggestMatchSize < matchSize) 
					{
						biggestMatchSize = matchSize;
						matchLeftIndex = i;
						matchRightIndex = matchingIndex;
					}
				}
			}

			int status = (matchLeftIndex==leftIndex || matchRightIndex==rightIndex) ? FileLine.NO_MATCH:FileLine.MODIFIED;
			int max = Math.min(matchLeftIndex,leftLines.length);
			for(int i=leftIndex;i<max;i++)
			{
				if (i<leftLines.length)
				{
					leftLines[i].setStatus(status);
					leftIndex++;
				}
			}
			max = Math.min(matchRightIndex,rightLines.length);
			for(int i=rightIndex;i<max;i++)
			{
				rightLines[i].setStatus(status);
				rightIndex++;
			}
		}
		return result;
	}

	/**
	 * gives the amount of contiguous matching lines, starting at the given indexes.
	 * if 'searchLimit' is set to -1, there is no other limit in the lookup than the
	 * arrays' sizes. Otherwise, the lookup is limited to the given value
	 *
	 * Watch out because different limits may give different diff results in some cases,
	 * and increasing the limit to find the biggest matching bloc does not necessarily
	 * lead to the minimum set of differences in the final diff result.
	 *
	 * Actually, this limitSearch should be just a bit more than the size of moved blocs
	 * to be able to distinguish them..
	 *
	 * @param leftLines FileLine[]
	 * @param rightLines FileLine[]
	 * @param leftIndex int
	 * @param rightIndex int
	 * @param limitSearch int
	 * @return int
	 */
	public static int getContiguousMatchingLines(FileLine leftLines[], FileLine rightLines[],
			int leftIndex, int rightIndex, int limitSearch, boolean ignoreLeadingSpaces){
		int result = 0;
		while((limitSearch==-1 || result<limitSearch)&&
				((leftIndex+result)<leftLines.length)&&
				((rightIndex+result)<rightLines.length)&&
				(leftLines[leftIndex+result].matches(rightLines[rightIndex+result],ignoreLeadingSpaces))){
			result++;
		}
		return result;
	}

	public static FileDiffResult quickDiff2(String leftFileContent, String rightFileContent, boolean ignoreLeadingSpaces) {
		ParsedFile leftFile = new ParsedFile(leftFileContent);
		ParsedFile rightFile = new ParsedFile(rightFileContent);
		FileDiffResult result = new FileDiffResult(leftFile,rightFile);
		lookForMatch(leftFile, rightFile, ignoreLeadingSpaces);
		lookForMatch(rightFile, leftFile, ignoreLeadingSpaces);
		FileLine leftLines[] = leftFile.getLines();
		FileLine rightLines[] = rightFile.getLines();
		int leftIndex = 0;
		int rightIndex = 0;
		while(leftIndex<leftLines.length && rightIndex<rightLines.length){
			// consume identical lines
			while(leftIndex<leftLines.length &&
					rightIndex<rightLines.length &&
					(leftLines[leftIndex].matches(rightLines[rightIndex],ignoreLeadingSpaces))){
				leftLines[leftIndex].setStatus(FileLine.UNCHANGED);
				rightLines[rightIndex].setStatus(FileLine.UNCHANGED);
				leftIndex++;
				rightIndex++;
			}
			// now find the lowest index on both sides where matching starts again. If both indexes
			// have increased, we can consider the skipped blocks on both sides as MODIFIED, otherwise,
			// the bloc for the increased index is NO_MATCH
			int matchLeftIndex = leftLines.length;
			int matchRightIndex = rightLines.length;

			int lowestBlocSize = Integer.MAX_VALUE;
			int lowestIndex = Integer.MAX_VALUE;
			for(int i=leftIndex;i<leftLines.length;i++){
				int matchingIndex = leftLines[i].indexInOtherVersion();
				int blocSize = (matchingIndex-rightIndex)+(i-leftIndex);
				if ((matchingIndex>-1) && (matchingIndex>=rightIndex) && (lowestBlocSize>blocSize) && (matchingIndex<lowestIndex)){
					lowestBlocSize = blocSize;
					lowestIndex = matchingIndex;
					matchLeftIndex = i;
					matchRightIndex = matchingIndex;
				}
			}

			int status = (matchLeftIndex==leftIndex || matchRightIndex==rightIndex) ? FileLine.NO_MATCH:FileLine.MODIFIED;
			int max = Math.min(matchLeftIndex,leftLines.length);
			for(int i=leftIndex;i<max;i++){
				if (i<leftLines.length){
					leftLines[i].setStatus(status);
					leftIndex++;
				}
			}
			max = Math.min(matchRightIndex,rightLines.length);
			for(int i=rightIndex;i<max;i++){
				rightLines[i].setStatus(status);
				rightIndex++;
			}
		}
		return result;
	}


	public MergeResult merge(String commonAncestor, String version1, String version2, Cancellable cancellable, boolean ignoreLeadingSpaces){
		MergeResult result = null;
		FileDiffResult diff1 = diff(commonAncestor, version1, cancellable, ignoreLeadingSpaces);
		FileDiffResult diff2 = diff(commonAncestor, version2, cancellable, ignoreLeadingSpaces);
		diff1 = Diff.format(diff1);
		diff2 = Diff.format(diff2);
		if (cancellable!=null && cancellable.isCancelled()){
			return null;
		}
		result = analyze(diff1,diff2);
		return result;
	}

	public static MergeResult quickMerge(String commonAncestor, String version1, String version2, boolean ignoreLeadingSpaces){
		MergeResult result = null;
		FileDiffResult diff1 = Diff.quickDiff(commonAncestor, version1, ignoreLeadingSpaces);
		FileDiffResult diff2 = Diff.quickDiff(commonAncestor, version2, ignoreLeadingSpaces);
		diff1 = Diff.format(diff1);
		diff2 = Diff.format(diff2);
		result = analyze(diff1,diff2);
		return result;
	}

	/**
	 * build the merge infos from the two diff with common ancestor
	 * here are all the cases handled:
	 *
	 * 1) unchanged-unchanged => unchanged (no conflict)
	 * 2) unchanged-modified  => modified (no conflict)
	 * 3) unchanged-inserted  => inserted (no conflict) // cannot happen anymore due to formatting
	 * 4) unchanged-deleted   => deleted (but warning)
	 *
	 * 5) modified-modified   => conflict
	 * 6) modified-inserted   => conflict
	 * 7) modified-deleted    => conflict
	 *
	 * 8) inserted-deleted    => insert and delete (no conflict) // cannot happen anymore due to formatting
	 * 9) inserted-inserted   => insert both (but warning on insert order)
	 *
	 * 10) deleted-deleted    => delete (no conflict)
	 *
	 * following cases can be handled by simple local loops:
	 * 1,3,4,8,9,10
	 * left 2,5,6,7: all the modified, which should be handled by bloc
	 *
	 * @param commonAncestor String
	 * @param version1 String
	 * @param version2 String
	 */
	public static MergeResult analyze(FileDiffResult diff1, FileDiffResult diff2){

		ArrayList<MergeResultItem> mergeItems = new ArrayList<MergeResultItem>();
		FileDiffResult formated[] = format(new FileDiffResult[]{diff1,diff2});
		diff1 = formated[0];
		diff2 = formated[1];

		FileLine ancestor1Lines[] = diff1.getLeftFile().getLines();
		FileLine leftLines[] = diff1.getRightFile().getLines();
		FileLine ancestor2Lines[] = diff2.getLeftFile().getLines();
		FileLine rightLines[] = diff2.getRightFile().getLines();
		ArrayList<FileLine> leftLinesArray = new ArrayList<FileLine>();
		ArrayList<FileLine> rightLinesArray = new ArrayList<FileLine>();

		int index=0;
		int max=leftLines.length;// since merge input has been formated, all arrays have the same length
		int bloc = 0;
		// mergeItems.add(new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,text,text,MergeResultItem.DefaultVersion.LEFT,lineCount));
		while (index<max) {
			ArrayList<FileLine> tempLeftLines = new ArrayList<FileLine>();
			ArrayList<FileLine> tempRightLines = new ArrayList<FileLine>();
			int leftStatus = leftLines[index].getStatus();
			int ancestor1Status = ancestor1Lines[index].getStatus();
			int rightStatus = rightLines[index].getStatus();
			int ancestor2Status = ancestor2Lines[index].getStatus();
			int lineCount = 0;

			// now create a block with identical status on each sides
			while(index<max &&
					leftStatus == leftLines[index].getStatus() &&
					ancestor1Status == ancestor1Lines[index].getStatus() &&
					rightStatus == rightLines[index].getStatus() &&
					ancestor2Status == ancestor2Lines[index].getStatus()){
				tempLeftLines.add(leftLines[index]);
				tempRightLines.add(rightLines[index]);
				index++;
				lineCount++;
			}

			// now status has changed, let's create the merge item with the right status
			// since formatted, if one ancestor status is NO_MATCH, it will be the same for the other

			// non conflictual blocs:
			if (ancestor1Status==FileLine.UNCHANGED && ancestor2Status==FileLine.UNCHANGED){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			else if (ancestor1Status==FileLine.UNCHANGED && ancestor2Status==FileLine.MODIFIED){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			else if (ancestor1Status==FileLine.UNCHANGED && ancestor2Status==FileLine.INSERTED_ON_OTHER_SIDE){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			else if (ancestor1Status==FileLine.MODIFIED && ancestor2Status==FileLine.UNCHANGED ){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			else if (ancestor1Status==FileLine.INSERTED_ON_OTHER_SIDE && ancestor2Status==FileLine.UNCHANGED){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			else if (ancestor1Status==FileLine.INSERTED_ON_OTHER_SIDE && ancestor2Status==FileLine.NO_MATCH){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			else if (ancestor1Status==FileLine.NO_MATCH && ancestor2Status==FileLine.INSERTED_ON_OTHER_SIDE){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			else if (ancestor1Status==FileLine.NO_MATCH && ancestor2Status==FileLine.MODIFIED){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			else if (ancestor1Status==FileLine.MODIFIED && ancestor2Status==FileLine.NO_MATCH){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			// deleted line only on one side, and left on the other: by default keep the line removed, but put a warning
			else if (ancestor1Status==FileLine.NO_MATCH && ancestor2Status==FileLine.UNCHANGED){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.WARNING_DELETE,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			else if (ancestor1Status==FileLine.UNCHANGED && ancestor2Status==FileLine.NO_MATCH){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.WARNING_DELETE,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			// 2 following cases do not happen, but the 2 previous instead..
			else if (ancestor1Status==FileLine.UNCHANGED && ancestor2Status==FileLine.DELETED_ON_OTHER_SIDE){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.WARNING_DELETE,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			else if (ancestor1Status==FileLine.DELETED_ON_OTHER_SIDE && ancestor2Status==FileLine.UNCHANGED){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.WARNING_DELETE,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			else if (ancestor1Status==FileLine.INSERTED_ON_OTHER_SIDE && ancestor2Status==FileLine.INSERTED_ON_OTHER_SIDE){
				if (buildText(tempLeftLines).equals(buildText(tempRightLines))){
					addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
				}
				else{
					addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.WARNING_ORDER,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
				}
			}
			else if (ancestor1Status==FileLine.MODIFIED && ancestor2Status==FileLine.MODIFIED){
				if (buildText(tempLeftLines).equals(buildText(tempRightLines))){
					addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.NO_CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
				}
				else{
					addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT, lineCount));
				}
			}
			// conflictual blocs:
			else if (ancestor1Status==FileLine.INSERTED_ON_OTHER_SIDE && ancestor2Status==FileLine.MODIFIED){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			else if (ancestor1Status==FileLine.MODIFIED && ancestor2Status==FileLine.INSERTED_ON_OTHER_SIDE){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			else if (ancestor1Status==FileLine.MODIFIED && ancestor2Status==FileLine.DELETED_ON_OTHER_SIDE){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.LEFT,lineCount));
			}
			else if (ancestor1Status==FileLine.DELETED_ON_OTHER_SIDE && ancestor2Status==FileLine.DELETED_ON_OTHER_SIDE){
				// this case never happen because on a diff, we have inserted lines on the left, and deleted on the right
				// and both diffs have on purpose the common ancestor on the left
			}
			else if (ancestor1Status==FileLine.DELETED_ON_OTHER_SIDE && ancestor2Status==FileLine.MODIFIED){
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			else if (ancestor1Status==FileLine.NO_MATCH && ancestor2Status==FileLine.NO_MATCH){
				// nothing to do
			}
			else{
				System.out.println("###################### One case forgoten !!! bloc: "+bloc+", lineCount: "+lineCount);
				System.out.println("leftStatus: "+FileLine.statusToString(leftStatus));
				System.out.println("ancestor1Status: "+FileLine.statusToString(ancestor1Status));
				System.out.println("rightStatus: "+FileLine.statusToString(rightStatus));
				System.out.println("ancestor2Status: "+FileLine.statusToString(ancestor2Status));
				// if there is any forgetten case, let's create a conflict, even if there is not
				addMergeItem(mergeItems,new MergeResultItem(MergeResultItem.Type.CONFLICT,tempLeftLines,tempRightLines,MergeResultItem.DefaultVersion.RIGHT,lineCount));
			}
			bloc++;
			leftLinesArray.addAll(tempLeftLines);
			rightLinesArray.addAll(tempRightLines);
		}
		mergeItems = mergeAppropriateItems(mergeItems);
		//		dumpMergeItems(mergeItems);
		MergeResult result = new MergeResult(mergeItems);
		return result;
	}

	@SuppressWarnings("unused")
	private static void dumpMergeItems(ArrayList<MergeResultItem> items){
		System.out.println("found "+items.size()+" merge items");
		for(int i=0;i<items.size();i++){
			MergeResultItem item = items.get(i);
			System.out.println(item.toString());
		}
	}

	private static void addMergeItem(ArrayList<MergeResultItem> items, MergeResultItem newItem){
		items.add(newItem);
	}

	private static ArrayList<MergeResultItem> mergeAppropriateItems(ArrayList<MergeResultItem> items){
		ArrayList<MergeResultItem> result = new ArrayList<MergeResultItem>();
		ArrayList<MergeResultItem> temp = new ArrayList<MergeResultItem>();

		// we need to merge items over all conflict items, and

		int firstIndex = 1;
		MergeResultItem initial = items.get(0);
		temp.add(initial);
		if (initial.matches()){
			// there cannot be 2 consecutive matches, since consecutive blocs are
			// necessarily different types
			handleMergeableItems(temp,result);
			firstIndex++;
			if (items.size()>1){
				initial = items.get(1);
				temp.add(initial);
			}
		}
		for (int i = firstIndex; i < items.size(); i++) {
			MergeResultItem next = items.get(i);
			if (next.matches()){
				// now we have reached a match region, we can flush the pending temp items
				handleMergeableItems(temp,result);
				initial = next;
				temp.add(initial);
			}
			else{
				// we are still on a bloc of mergeable items
				if (initial.couldMerge(next)){
					temp.add(next);
					initial=next;
				}
				else{
					// now, we have a set of mergeable items. We only do the merge
					handleMergeableItems(temp, result);
					initial = next;
					temp.add(initial);
				}
			}
		}
		// handle remaining items
		handleMergeableItems(temp,result);

		return result;
	}

	private static void handleMergeableItems(ArrayList<MergeResultItem> temp, ArrayList<MergeResultItem> result){
		boolean conflict = false;
		for(int i=0;!conflict && i<temp.size();i++){
			conflict = temp.get(i).isConflict();
		}
		if (!conflict){
			result.addAll(temp);
			temp.clear();
		}
		else{
			MergeResultItem mergedItems = MergeResultItem.merge(MergeResultItem.Type.CONFLICT,temp);
			result.add(mergedItems);
			temp.clear();
		}
	}

	public static String buildText(ArrayList<FileLine> fileLines){
		String eol = System.getProperty("line.separator");
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<fileLines.size();i++){
			buf.append(fileLines.get(i).getContent());
			if (i<(fileLines.size()-1)){
				buf.append(eol);
			}
		}
		return buf.toString();
	}

	private static void lookForMatch(ParsedFile inputFile, ParsedFile referenceFile, boolean ignoreLeadingSpaces){
		FileLine inputLines[] = inputFile.getLines();
		FileLine referenceLines[] = referenceFile.getLines();
		for(int i=0;i<inputLines.length;i++){
			boolean match = false;
			for (int j = 0; !match && j < referenceLines.length; j++) {
				if (referenceLines[j].isMatchFound()==false){
					if (inputLines[i].matches(referenceLines[j], ignoreLeadingSpaces)) {
						inputLines[i].setIndexInOtherVersion(j);
						referenceLines[j].setIndexInOtherVersion(i);
						match = true;
					}
				}
			}
			// default status for lines is "no match", so if (match==false) nothing special to do
		}
	}

	/**
	 *
	 * This method formats the 2 diff inputs for merge algorithm in a way
	 * all arrays have the same number of lines
	 */
	private static FileDiffResult[] format(FileDiffResult mergeInput[]){
		FileDiffResult[] result = new FileDiffResult[2];
		if (mergeInput.length==2){
			int rightOffset = 0;
			int leftOffset = 0;
			FileLine ancestor1Lines[] = mergeInput[0].getLeftFile().getLines();
			FileLine leftLines[] = mergeInput[0].getRightFile().getLines();
			FileLine ancestor2Lines[] = mergeInput[1].getLeftFile().getLines();
			FileLine rightLines[] = mergeInput[1].getRightFile().getLines();
			ArrayList<FileLine> leftLinesArray = new ArrayList<FileLine>();
			ArrayList<FileLine> ancestor1LinesArray = new ArrayList<FileLine>();
			ArrayList<FileLine> rightLinesArray = new ArrayList<FileLine>();
			ArrayList<FileLine> ancestor2LinesArray = new ArrayList<FileLine>();

			if (ancestor1Lines.length!=leftLines.length || ancestor2Lines.length!=rightLines.length){
				System.err.println("error on input arrays sizes for merge formatting");
			}

			int leftAdded = 0;
			int rightAdded = 0;
			int commonNoMatch = 0;

			while(leftOffset<ancestor1Lines.length && rightOffset<ancestor2Lines.length){
				if ((ancestor1Lines[leftOffset].getIndex()==ancestor2Lines[rightOffset].getIndex())){
					leftLinesArray.add(leftLines[leftOffset]);
					ancestor1LinesArray.add(ancestor1Lines[leftOffset]);
					rightLinesArray.add(rightLines[rightOffset]);
					ancestor2LinesArray.add(ancestor2Lines[rightOffset]);
					if (ancestor1Lines[leftOffset].getIndex()==-1){
						commonNoMatch++;
					}
					leftOffset++;
					rightOffset++;
				}
				else if (ancestor1Lines[leftOffset].getIndex()==-1 && ancestor2Lines[rightOffset].getIndex()!=-1){
					leftLinesArray.add(leftLines[leftOffset]);
					ancestor1LinesArray.add(ancestor1Lines[leftOffset]);
					rightLinesArray.add(FileLine.getFormattingLine(FileLine.UNCHANGED));
					ancestor2LinesArray.add(FileLine.getFormattingLine(FileLine.UNCHANGED));
					leftOffset++;
					rightAdded++;
				}
				else if (ancestor1Lines[leftOffset].getIndex()!=-1 && ancestor2Lines[rightOffset].getIndex()==-1){
					rightLinesArray.add(rightLines[rightOffset]);
					ancestor2LinesArray.add(ancestor2Lines[rightOffset]);
					leftLinesArray.add(FileLine.getFormattingLine(FileLine.UNCHANGED));
					ancestor1LinesArray.add(FileLine.getFormattingLine(FileLine.UNCHANGED));
					rightOffset++;
					leftAdded++;
				}
				else{
					System.err.println("case not handled !!?");
				}
			}

			// it may happen we reach the end of one diff before the other in some cases so let's fill with empty line
			// the one who has not finished
			if (leftOffset<ancestor1Lines.length){
				while(leftOffset<ancestor1Lines.length){
					leftLinesArray.add(leftLines[leftOffset]);
					ancestor1LinesArray.add(ancestor1Lines[leftOffset]);
					rightLinesArray.add(FileLine.getFormattingLine(FileLine.UNCHANGED));
					ancestor2LinesArray.add(FileLine.getFormattingLine(FileLine.UNCHANGED));
					leftOffset++;
				}
			}
			else if (rightOffset<ancestor2Lines.length){
				while(rightOffset<ancestor2Lines.length){
					rightLinesArray.add(rightLines[rightOffset]);
					ancestor2LinesArray.add(ancestor2Lines[rightOffset]);
					leftLinesArray.add(FileLine.getFormattingLine(FileLine.UNCHANGED));
					ancestor1LinesArray.add(FileLine.getFormattingLine(FileLine.UNCHANGED));
					rightOffset++;
					leftAdded++;
				}
			}

			if (leftLinesArray.size()!=rightLinesArray.size()){
				System.err.println("error on merge input formatting");
				System.err.println("left final size: "+leftLinesArray.size());
				System.err.println("right final size: "+rightLinesArray.size());
				System.err.println("left added: "+leftAdded);
				System.err.println("right added: "+rightAdded);
				System.err.println("commonNoMatch: "+commonNoMatch);
			}

			// now create the result
			result[0] = new FileDiffResult(new ParsedFile(ancestor1LinesArray),new ParsedFile(leftLinesArray));
			result[1] = new FileDiffResult(new ParsedFile(ancestor2LinesArray),new ParsedFile(rightLinesArray));

		}
		return result;
	}

	/**
	 * this method formats arrays from diff computing in order to get
	 * 2 arrays of the same length. In other words, it inserts in each
	 * arrays the empty lines necessary to get row matching elements
	 *
	 * @param diffResult FileDiffResult
	 * @return FileDiffResult
	 */
	public static FileDiffResult format(FileDiffResult diffResult) {
		FileLine leftLines[] = diffResult.getLeftFile().getLines();
		FileLine rightLines[] = diffResult.getRightFile().getLines();
		int leftIndex = 0;
		int rightIndex = 0;

		ArrayList<FileLine> leftLinesArray = new ArrayList<FileLine>();
		ArrayList<FileLine> rightLinesArray = new ArrayList<FileLine>();

		boolean consumed = true;
		while (consumed) 
		{
			consumed = false;
			// try to consume identical lines
			// here, on the opposite to what is done if Diff, we need to test both
			// arrays, beause if a line has been added on the right, next line on the left
			// will already be considered as unchanged, while we should insert a blank before
			while (((leftIndex < leftLines.length) || (rightIndex < rightLines.length)) &&
					((leftIndex == leftLines.length) || (leftLines[leftIndex].getStatus() == FileLine.UNCHANGED)) &&
					((rightIndex == rightLines.length) || (rightLines[rightIndex].getStatus() == FileLine.UNCHANGED))) 
			{

				if (leftIndex<leftLines.length)
				{
					leftLinesArray.add(leftLines[leftIndex]);
					leftIndex++;
				}
				if (rightIndex<rightLines.length)
				{
					rightLinesArray.add(rightLines[rightIndex]);
					rightIndex++;
				}
				consumed = true;
			}

			// now consume modified lines on both sides
			while (((leftIndex < leftLines.length) || (rightIndex < rightLines.length)) &&
					((leftIndex == leftLines.length) || (leftLines[leftIndex].getStatus() == FileLine.MODIFIED)) &&
					((rightIndex == rightLines.length) || (rightLines[rightIndex].getStatus() == FileLine.MODIFIED))) 
			{

				if (leftIndex<leftLines.length)
				{
					leftLinesArray.add(leftLines[leftIndex]);
					leftIndex++;
				}
				else
				{
					leftLinesArray.add(FileLine.getFormattingLine(FileLine.MODIFIED));
				}
				if (rightIndex<rightLines.length)
				{
					rightLinesArray.add(rightLines[rightIndex]);
					rightIndex++;
				}
				else
				{
					rightLinesArray.add(FileLine.getFormattingLine(FileLine.MODIFIED));
				}
				consumed = true;
			}

			// now consume modified lines only on the left
			while (((leftIndex < leftLines.length) || (rightIndex < rightLines.length)) &&
					((leftIndex == leftLines.length) || (leftLines[leftIndex].getStatus() == FileLine.MODIFIED)) &&
					((rightIndex == rightLines.length) || (rightLines[rightIndex].getStatus() == FileLine.UNCHANGED))) 
			{

				if (leftIndex<leftLines.length)
				{
					leftLinesArray.add(leftLines[leftIndex]);
					leftIndex++;
				}
				if (rightIndex<rightLines.length)
				{
					rightLinesArray.add(FileLine.getFormattingLine(FileLine.MODIFIED));
				}
				consumed = true;
			}

			// now consume modified lines only on the right
			while (((leftIndex < leftLines.length) || (rightIndex < rightLines.length)) &&
					((leftIndex == leftLines.length) || (leftLines[leftIndex].getStatus() == FileLine.UNCHANGED)) &&
					((rightIndex == rightLines.length) || (rightLines[rightIndex].getStatus() == FileLine.MODIFIED))) 
			{

				if (leftIndex<leftLines.length)
				{
					leftLinesArray.add(FileLine.getFormattingLine(FileLine.MODIFIED));
				}
				if (rightIndex<rightLines.length){
					rightLinesArray.add(rightLines[rightIndex]);
					rightIndex++;
				}
				consumed = true;
			}

			// consume non matching lines on the left
			while ( (leftIndex < leftLines.length) &&
					(leftLines[leftIndex].getStatus() == FileLine.NO_MATCH)) 
			{
				leftLinesArray.add(leftLines[leftIndex]);
				leftIndex++;
				rightLinesArray.add(FileLine.getFormattingLine(FileLine.INSERTED_ON_OTHER_SIDE));
				consumed = true;
			}
			// consume non matching lines on the right
			while ( (rightIndex < rightLines.length) &&
					(rightLines[rightIndex].getStatus() == FileLine.NO_MATCH)) 
			{
				rightLinesArray.add(rightLines[rightIndex]);
				rightIndex++;
				leftLinesArray.add(FileLine.getFormattingLine(FileLine.INSERTED_ON_OTHER_SIDE));
				consumed = true;
			}

		}

		if (leftIndex<leftLines.length || rightIndex<rightLines.length)
		{
			System.err.println("not all lines consumed !!!");
		}
		FileDiffResult result = new FileDiffResult(new ParsedFile(leftLinesArray),new ParsedFile(rightLinesArray));
		return result;
	}
}

