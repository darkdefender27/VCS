package com.diff.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.diff.core.Cancellable;
import com.diff.core.DirContentStatus.Status;

public class FolderComparator {
	
	private static boolean containsOneDiff(List<DirContentStatus> children){
		boolean result = false;
		for(int i=0;!result && i<children.size();i++){
			DirContentStatus child = children.get(i);
			if (child.getStatus()==Status.NEW || child.getStatus()==Status.MODIFIED){
				result = true;
			}
		}
		return result;
	}
	
	public static DirDiffResult compareFolders(File leftFolder, File rightFolder, Cancellable cancellable) throws IOException{
		DirDiffResult result = new DirDiffResult(leftFolder,rightFolder);
		if (!leftFolder.exists()){
			throw new IOException("left folder does not exist");
		}		
		if (!rightFolder.exists()){
			throw new IOException("right folder does not exist");
		}
		if (!leftFolder.isDirectory()){
			throw new IOException("left file is not a folder");
		}
		if (!rightFolder.isDirectory()){
			throw new IOException("right file is not a folder");
		}
		HashMap<String, File> leftMap = buildMap(leftFolder);
		HashMap<String, File> rightMap = buildMap(rightFolder);

		Iterator<Map.Entry<String,File>> leftFiles = leftMap.entrySet().iterator();
		while(leftFiles.hasNext() && !cancellable.isCancelled()){
		  Map.Entry<String,File> theLeftFile = leftFiles.next();
			String leftFileName = theLeftFile.getKey();
			File leftFile = theLeftFile.getValue();
			File rightFile = rightMap.get(leftFileName);
			if (rightFile==null){
				DirContentStatus leftFileStatus = result.getLeftContent().addFileNew(leftFile);
				if (leftFile.isDirectory()){
					fillNewFolder(leftFileStatus);
				}
			}
			else{
				if (leftFile.isDirectory()!=rightFile.isDirectory()){
					// not the same type
					DirContentStatus leftFileStatus = result.getLeftContent().addFileNew(leftFile);
					if (leftFile.isDirectory()){
						fillNewFolder(leftFileStatus);
					}
					DirContentStatus rightFileStatus = result.getRightContent().addFileNew(rightFile);
					if (rightFile.isDirectory()){
						fillNewFolder(rightFileStatus);
					}
				}
				else{
					// same type
					if (leftFile.isDirectory()){
						// both are folders
						DirDiffResult temp = compareFolders(leftFile,rightFile,cancellable);
						DirContentStatus leftChild = result.getLeftContent().addFileEqual(leftFile);
						DirContentStatus rightChild = result.getRightContent().addFileEqual(rightFile);
						// propagate file status to parent folder
						if (containsOneDiff(temp.getLeftContent().getChildren()) || containsOneDiff(temp.getRightContent().getChildren())){
							leftChild.setStatus(Status.MODIFIED);
							rightChild.setStatus(Status.MODIFIED);
						}
						leftChild.addAll(temp.getLeftContent());
						rightChild.addAll(temp.getRightContent());
					}
					else{
						// both are files
						boolean areEqual = areFileEqual(leftFile,rightFile,cancellable);
						if (areEqual){
							result.getLeftContent().addFileEqual(leftFile);
							result.getRightContent().addFileEqual(rightFile);
						}
						else{
							result.getLeftContent().addFileModified(leftFile, rightFile);
							result.getRightContent().addFileModified(rightFile, leftFile);
						}
					}
				}				
			}
		}
		// now, we must do the same on the other way round, just to add
		// files/folders on right side which do not exist on left side
		Iterator<Map.Entry<String,File>> rightFiles = rightMap.entrySet().iterator();
		while(rightFiles.hasNext() && !cancellable.isCancelled()){
		  Map.Entry<String,File> theRightFile=rightFiles.next();
			String rightFileName = theRightFile.getKey();
			File rightFile = theRightFile.getValue();
      File leftFile = leftMap.get(rightFileName);
			if (leftFile==null){
				DirContentStatus rightFileStatus = result.getRightContent().addFileNew(rightFile);
				if (rightFile.isDirectory()){
					fillNewFolder(rightFileStatus);
				}
			}			
		}		
		return result;
	}
	
	private static void fillNewFolder(DirContentStatus newFolder){
		File children[] = newFolder.getFile().listFiles();
		for(File child:children){
			DirContentStatus dirContent = newFolder.addFileNew(child);
			if (child.isDirectory()){
				fillNewFolder(dirContent);
			}
		}
	}
	
	private static HashMap<String, File> buildMap(File folder){
		HashMap<String, File> result = new HashMap<String, File>();
		File[] children = folder.listFiles();
		if (children!=null){
			for(File child:children){
				result.put(child.getName(), child);
			}
		}
		return result;
	}
	
	private static boolean areFileEqual(File leftFile, File rightFile, Cancellable cancellable) throws IOException{
		boolean result = true;
		if (leftFile.length()==rightFile.length()){
			InputStream is1 = null;
			InputStream is2 = null;
			try{
				is1 = new BufferedInputStream(new FileInputStream(leftFile));
			 	is2 = new BufferedInputStream(new FileInputStream(rightFile));
			 	
			 	int c1 = 0;
			 	int c2 = 0;
			 	// no need to test both length read, since files are the same length
			 	while(result && c1>-1 && !cancellable.isCancelled()){
			 		c1 = is1.read();
			 		c2 = is2.read();
			 		if (c1!=c2){
			 			result = false;
			 		}
			 	}
			}
			finally{
				if (is1!=null){
					try{
						is1.close();
					}
					catch(Exception ex){}
				}
				if (is2!=null){
					try{
						is2.close();
					}
					catch(Exception ex){}
				}
			}
		}
		else{
			result = false;
		}
		return result;
	}
}
