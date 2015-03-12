package vcs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import objects.AbstractVCSTree;
import objects.VCSBlob;
import objects.VCSCommit;
import objects.VCSTree;

public class Operations {

	public static String getBranchesFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER +"/"+Constants.BRANCH_FOLDER);
		return toCreateDirPath.toString();
	}
	public static String getHooksFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER+"/"+Constants.HOOKS_FOLDER);
		return toCreateDirPath.toString();
	}
	public static String getInfoFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER+"/"+Constants.INFO_FOLDER);
		return toCreateDirPath.toString();
	}
	public static String getLogsFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER+"/"+Constants.LOGS_FOLDER);
		return toCreateDirPath.toString();
	}
	public static String getObjectsFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER+"/"+Constants.OBJECTS_FOLDER);
		return toCreateDirPath.toString();
	}
	public static String getRefsFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER+"/"+Constants.REFS_FOLDER);
		return toCreateDirPath.toString();
	}
	public static String getHeadsFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER+"/"+Constants.REFS_FOLDER +"/"+Constants.HEADS_FOLDER);
		return toCreateDirPath.toString(); 
	}
	public static String getTagsFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER+"/"+Constants.REFS_FOLDER +"/"+Constants.TAGS_FOLDER);
		return toCreateDirPath.toString();
	}
	public static boolean isVCSRepository(String workingDir){
		return false;
	}
	public boolean initRepository(String workingDirectory){
		boolean returnStatus = false;
		Path workingDirPath=Paths.get(workingDirectory);
		if(Files.exists(workingDirPath))
		{
			Path vcsPath=Paths.get(workingDirectory+".vcs");
			if(Files.exists(vcsPath))
			{
				//already ini repo
			}
			else
			{
				boolean vcsFolderCreated=new File(vcsPath.toString()).mkdir();
				if(vcsFolderCreated)
				{
					getBranchesFolder(workingDirectory);
					boolean branchesFolderCreated=new File(Operations.getBranchesFolder(workingDirectory)).mkdir();
					
					getHooksFolder(workingDirectory);
					boolean hooksFolderCreated=new File(Operations.getHooksFolder(workingDirectory)).mkdir();
					
					getInfoFolder(workingDirectory);
					boolean infoFolderCreated=new File(Operations.getInfoFolder(workingDirectory)).mkdir();
					
					getLogsFolder(workingDirectory);
					boolean logsFolderCreated=new File(Operations.getLogsFolder(workingDirectory)).mkdir();
					
					getObjectsFolder(workingDirectory);
					boolean objectsFolderCreated=new File(Operations.getObjectsFolder(workingDirectory)).mkdir();
					
					getRefsFolder(workingDirectory);
					boolean refsFolderCreated=new File(Operations.getRefsFolder(workingDirectory)).mkdir();
					
					getHeadsFolder(workingDirectory);
					boolean headsFolderCreated=new File(Operations.getHeadsFolder(workingDirectory)).mkdir();
					
					getTagsFolder(workingDirectory);
					boolean tagsFolderCreated=new File(Operations.getTagsFolder(workingDirectory)).mkdir();
					
					if(branchesFolderCreated && hooksFolderCreated && infoFolderCreated && logsFolderCreated && objectsFolderCreated && refsFolderCreated && headsFolderCreated && tagsFolderCreated)
					{
						System.out.println("Bare repository initialised");
						returnStatus = true;
					}
				}
			}
		}
		return returnStatus;
	}
	
	public String commit(String[] stagedFiles,VCSCommit parentCommit, String message,String author,String committer,String workingDir)
	{
		if(parentCommit==null){
			VCSTree workDir = new VCSTree("workDir", workingDir, workingDir);
			VCSTree currentTree = workDir;
			VCSTree eleAtPath = null;
			VCSBlob lastEleAtPath = null;
			for(int i=0;i<stagedFiles.length;i++){
				currentTree = workDir;
				StringBuilder overallPath = new StringBuilder();
				String[] path = stagedFiles[i].split("/");
				overallPath.append(workingDir);
				for(int j=0;j<path.length;j++){
					//System.out.println(path[j]);
					if(j != 0 )overallPath.append("/");
					overallPath.append(path[j]);
					if(j!=path.length-1){
						AbstractVCSTree searchedItem=currentTree.getIfExist(path[j], "tree");
						if(searchedItem ==null){
							//System.out.println("NOT NULL"+ path[j]);
							eleAtPath = new VCSTree(path[j],overallPath.toString(), workingDir);
							currentTree.addItem(eleAtPath);
							currentTree = eleAtPath;
						}else currentTree = (VCSTree)searchedItem;
					}
					else{
						AbstractVCSTree searchedItem=currentTree.getIfExist(path[j], "blob");
						
						if(searchedItem==null){
							//System.out.println("NOT NULL FILE"+ path[j]);
							lastEleAtPath = new VCSBlob(path[j], overallPath.toString(), workingDir);
							currentTree.addItem(lastEleAtPath);
							//System.out.println("here " +eleAtPath.printTree());
						}
					}
				}
			}
			System.out.println(workDir.printTree(0));
			VCSCommit iniCommit = new VCSCommit(workingDir, null, workDir, message, author, committer);
			iniCommit.writeCommitToDisk();
			return iniCommit.getObjectHash();
		}
		else
		{
			
		}
		return null;
	}
}
