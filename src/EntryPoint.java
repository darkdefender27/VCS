import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.diff.core.Diff;
import com.diff.core.FileDiffResult;
import com.diff.core.MergeResult;


@SuppressWarnings("unused")
public class EntryPoint 
{
	static int unchanged=0;
	static int modifiedLeft=0;
	static int modifiedRight=0;
	static int noMatchLeft=0;
	static int noMatchRight=0;
	static int totalModifications;
	public static void main(String[] args)
	{
		String file1Contents, file2Contents,file3Contents;
		file1Contents = readFileIntoString("C:/Users/Ambarish/Desktop/temp/1.txt");
		file2Contents = readFileIntoString("C:/Users/Ambarish/Desktop/temp/1.txt");
		/*file2Contents = readFileIntoString("C:/Users/Ambarish/Desktop/project/1.txt");
		file3Contents = readFileIntoString("C:/Users/Ambarish/Desktop/project/VCS/1.txt");*/
		// System.out.println("here");
		/*FileDiffResult result = null;
		try 
		{
			result = Diff.diff(file1Contents, file2Contents, null, false);
			System.out.println(result.getLineResult().getNoOfLinesAdded() + " lines inserted ");
			System.out.println(result.getLineResult().getNoOfLinesDeleted() + " lines deleted ");
			System.out.println(result.getLineResult().getNoOfLinesUnmodified() + " lines unchanged ");
		}
		catch (Exception e) 
		{
			System.out.println("here");
		}*/
		/*MergeResult mr=Diff.merge(file3Contents, file1Contents, file2Contents, null, false);
		System.out.println(mr.getDefaultMergedResult());*/
	}
	
	/*
	private static void countLinesUnchangedModified(FileDiffResult result) 
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
	*/

	public static String readFileIntoString(String completeFileName)
	{
		String retVal=null;
		try 
		{
			retVal=new String(Files.readAllBytes(Paths.get(completeFileName)));
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}
}
