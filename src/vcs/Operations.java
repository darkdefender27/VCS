package vcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.diff.core.Diff;
import com.diff.core.FileDiffResult;
import com.diff.core.MergeResult;

import logger.VCSLogger;
import objects.AbstractVCSTree;
import objects.VCSBlob;
import objects.VCSCommit;
import objects.VCSTree;
import vcs.Constants;
//import network.HashUtil;
import java.lang.System;

public class Operations {

	public static String getBranchesFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.BRANCH_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getHooksFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.HOOKS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getInfoFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.INFO_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getLogsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.LOGS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getObjectsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.OBJECTS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getRefsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.REFS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getHeadsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.REFS_FOLDER + "/" + Constants.HEADS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static String getTagsFolder(String workingDirectory) {
		Path toCreateDirPath = Paths.get(workingDirectory + Constants.VCSFOLDER
				+ "/" + Constants.REFS_FOLDER + "/" + Constants.TAGS_FOLDER);
		return toCreateDirPath.toString();
	}

	public static boolean isVCSRepository(String workingDir) {
		return false;
	}

	
	/*public static String getConfigFolder(String workingDirectory) {
		Path toCreateDirPath=Paths.get(workingDirectory+Constants.VCSFOLDER +"/");
		return toCreateDirPath.toString();
	}*/
	
	public boolean initRepository(String workingDirectory){
		boolean returnStatus = false;
		Path workingDirPath=Paths.get(workingDirectory);
		if(Files.exists(workingDirPath))
		{
			Path vcsPath=Paths.get(workingDirectory+".vcs");
			if(Files.exists(vcsPath))
			{
				//repository is already initialized.
			}
			else
			{
				String repoName = null;
				String sample = vcsPath.toString();
				if(sample.contains("\\"))
				{
					sample=sample.replace("\\", "/");
				}
				String delims = "[/]";
				String[] tokens = sample.split(delims);
				System.out.println("tokens.length "+(tokens.length - 2) +"    "+sample);
				repoName = tokens[tokens.length - 2];
				
				//Create a File or fetch it (if already created) from user.home
				String userHomeDir = System.getProperty("user.dir");
				//VCSLogger.infoLogToCmd("User Home Directory: " + userHomeDir);

				//repoListHolder contains a List to all the local repositories initiated.
				File repoListHolder = new File(userHomeDir + "/repoListHolder.txt");
				
				try {
					
					//New File created otherwise else block is executed.
					boolean flag = repoListHolder.createNewFile();
					
					if(flag) {
						System.out.println("File repoListHolder created successfully.");
					}
					else {
						System.out.println("File repoListHolder already exists.");
					}
					
					//Writing to the File. FileWriter(File, boolean(append?) )
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(repoListHolder, true)))) {
					    out.println(repoName + ".vcs" + " " + workingDirectory); 
					    //Initially was vcsPath.toString();
					    // Format stored: repoName /Absolute/Path/to/the/repository/
					}
					catch (IOException e) {
						VCSLogger.infoLogToCmd("Data write to repoListHolder failed!");
					}
					
				} 
				catch (IOException e) {
					// TODO: handle exception
					VCSLogger.infoLogToCmd("Error in creating the File repoListHolder: " + e);
				} 
				
				
				boolean vcsFolderCreated=new File(vcsPath.toString()).mkdir();
				if(vcsFolderCreated)
				{
					getBranchesFolder(workingDirectory);
					boolean branchesFolderCreated = new File(
							Operations.getBranchesFolder(workingDirectory))
							.mkdir();

					getHooksFolder(workingDirectory);
					boolean hooksFolderCreated = new File(
							Operations.getHooksFolder(workingDirectory))
							.mkdir();

					getInfoFolder(workingDirectory);
					boolean infoFolderCreated = new File(
							Operations.getInfoFolder(workingDirectory)).mkdir();

					getLogsFolder(workingDirectory);
					boolean logsFolderCreated = new File(
							Operations.getLogsFolder(workingDirectory)).mkdir();

					getObjectsFolder(workingDirectory);
					boolean objectsFolderCreated = new File(
							Operations.getObjectsFolder(workingDirectory))
							.mkdir();

					getRefsFolder(workingDirectory);
					boolean refsFolderCreated = new File(
							Operations.getRefsFolder(workingDirectory)).mkdir();

					getHeadsFolder(workingDirectory);
					boolean headsFolderCreated = new File(
							Operations.getHeadsFolder(workingDirectory))
							.mkdir();

					getTagsFolder(workingDirectory);

					boolean tagsFolderCreated=new File(Operations.getTagsFolder(workingDirectory)).mkdir();
					
					//getConfigFolder(workingDirectory);
					boolean configCreated = false;
					try {
						configCreated = new File(workingDirectory + Constants.VCSFOLDER + "/config").createNewFile();
						writeConfig(workingDirectory);
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						VCSLogger.infoLogToCmd("UNABLE TO CREATE THE CONFIG FILE: " + e);
					}
					
					if(branchesFolderCreated && hooksFolderCreated && infoFolderCreated && logsFolderCreated && objectsFolderCreated && refsFolderCreated && headsFolderCreated && tagsFolderCreated && configCreated)
					{
						VCSLogger.infoLogToCmd("Bare repository initialised in " + workingDirectory);
						returnStatus = true;
					}
				}
			}
		}
		return returnStatus;
	}
	
	public boolean writeConfig(String workingDir) throws IOException
	{
		File config = new File(workingDir + Constants.VCSFOLDER + "/config");
		boolean status;
			
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(config, true)))) {
		    out.println("[core]\n");
		    //[remote 'origin'] url = repoUrl or http://127.0.0.1:8080/VCSD_1_1.vcs
		    status = true;
		}
		catch (IOException e) {
			status = false;
			VCSLogger.infoLogToCmd("DATA WRITES TO CONFIG FILE FAILED!");
		}
		return status;
	}	
	
	public VCSCommit getHead(String workingDir) throws IOException
	{
		BufferedReader br = null;
		File head = new File(getHeadsFolder(workingDir) + "/head");
		VCSCommit parent = null;
		String sCurrentLine;
		if (head.exists()) {
			br = new BufferedReader(new FileReader(getHeadsFolder(workingDir)
					+ "/head"));
			while ((sCurrentLine = br.readLine()) != null) {
				parent = new VCSCommit(sCurrentLine, workingDir,
						VCSCommit.IMPORT_TREE);
			}
			br.close();
		}
		return parent;
	}

	public boolean writeHead(String workingDir, String commitHash)
			throws IOException {
		File head = new File(getHeadsFolder(workingDir) + "/head");
		FileWriter fileWritter = new FileWriter(head, false);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(commitHash);
		bufferWritter.close();
		return true;
	}	

	public String commit(String[] stagedFiles,VCSCommit parentCommit, String message,String author,String committer,String workingDir)
	{
		VCSTree workDir = new VCSTree("workDir", workingDir, workingDir);
		workDir.setModified(true);
		VCSTree currentTree = workDir;
		VCSTree eleAtPath = null;
		VCSBlob lastEleAtPath = null;
		int noOflinesInserted=0,noOfLinesDeleted=0;
		for(int i=0;i<stagedFiles.length;i++)
		{
			currentTree = workDir;
			StringBuilder overallPath = new StringBuilder();
			String[] path = stagedFiles[i].split("/");
			overallPath.append(workingDir);
			System.out.println("overall path" +overallPath);
				for(int j=0;j<path.length;j++)
				{
					if(j != 0)
					{
						overallPath.append("/");
					}
					overallPath.append(path[j]);
					if(j!=path.length-1)
					{
						AbstractVCSTree searchedItem=currentTree.getIfExist(path[j], "tree");
						if(searchedItem ==null)
						{
							eleAtPath = new VCSTree(path[j],overallPath.toString(), workingDir);
							eleAtPath.setModified(true);
							currentTree.addItem(eleAtPath);
							currentTree = eleAtPath;
						}
						else
						{
							currentTree = (VCSTree)searchedItem;
						}
					}
					else
					{
						AbstractVCSTree searchedItem=currentTree.getIfExist(path[j], "blob");
						if(searchedItem==null)
						{
							//System.out.println("NOT NULL FILE"+ path[j]);
							lastEleAtPath = new VCSBlob(path[j], overallPath.toString(), workingDir);
							//overall path 
							//check if the file exists in parent commit and do diff
							lastEleAtPath.setModified(true);
							currentTree.addItem(lastEleAtPath);
							//System.out.println("here " +eleAtPath.printTree());
						}
					}
				}
			if(parentCommit!=null && parentCommit.getTree()!=null)
			{
				AbstractVCSTree obj=parentCommit.getTree().findTreeIfExist(stagedFiles[i], 0);
				VCSBlob b=(VCSBlob)obj;
				String fullFileName=null;
				if(b!=null)
				{
					//System.out.println(overallPath+"  parent commit's filename "+b.getObjectHash());
					fullFileName=b.writeTempFile(workingDir+"/"+Constants.VCSFOLDER+"/"+Constants.TEMP_FOLDER +"/",workingDir);
				}
				//do diff here
				if(fullFileName!=null)
				{
					//System.out.println(workingDir+"/"+stagedFiles[i]+" "+fullFileName);
					Diff diffObj=new Diff();
					FileDiffResult result=diffObj.diff(readFileIntoString(fullFileName),readFileIntoString(workingDir+"/"+stagedFiles[i]), null, false);
					noOfLinesDeleted+=result.getLineResult().getNoOfLinesDeleted();
					noOflinesInserted+=result.getLineResult().getNoOfLinesAdded();
					result=null;
					File f=new File(fullFileName);
					f.delete();
				}
				else
				{
					Diff diffObj=new Diff();
					FileDiffResult result=diffObj.diff("",readFileIntoString(workingDir+stagedFiles[i]), null, false);
					result.getLineResult().setNoOfLinesDeleted(result.getLineResult().getNoOfLinesDeleted()-1);
					noOfLinesDeleted+=result.getLineResult().getNoOfLinesDeleted();
					noOflinesInserted+=result.getLineResult().getNoOfLinesAdded();
					result=null;
				}
			}
			else if(parentCommit==null)
			{
				Diff diffObj=new Diff();
				
				FileDiffResult result=diffObj.diff("",readFileIntoString(workingDir+stagedFiles[i]), null, false);
				result.getLineResult().setNoOfLinesDeleted(result.getLineResult().getNoOfLinesDeleted()-1);
				//System.out.println("stagedfiles[i] "+stagedFiles[i] +" deleted "+result.getLineResult().getNoOfLinesDeleted());
				noOfLinesDeleted+=result.getLineResult().getNoOfLinesDeleted();
				noOflinesInserted+=result.getLineResult().getNoOfLinesAdded();
				result=null;
			}
		}
		if(parentCommit!=null)
		{
			currentTree = workDir;
			VCSTree parentTree = parentCommit.getTree();
			VCSLogger.debugLogToCmd("Operations#commit#parentTree\n",parentTree.printTree(0));
			inorder(currentTree,parentTree,"./",workingDir);
		}
		
		VCSLogger.debugLogToCmd("Operations#commit#newTree\n",workDir.printTree(0));
		VCSCommit iniCommit = new VCSCommit(workingDir, parentCommit, workDir, message, author, committer);
		iniCommit.setNoOfLinesInserted(noOflinesInserted);
		iniCommit.setNoOfLinesDeleted(noOfLinesDeleted);
		
		boolean done=writeToDeveloperList(committer,workingDir);
		
		String branchName=getCurrentBranchName(workingDir);
		try 
		{
			writeBranchHead(workingDir, iniCommit.getObjectHash(), branchName);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iniCommit.setBranchName(branchName);
		
		
		System.out.println("branchName= "+iniCommit.getBranchName());
		System.out.println("lines added " +noOflinesInserted+" lines deleted "+noOfLinesDeleted);
		iniCommit.setCommitTimestamp(System.currentTimeMillis());
		iniCommit.writeCommitToDisk();
		System.out.println("commit hash	"+iniCommit.getObjectHash());
		VCSLogger.infoLogToCmd(iniCommit.getTree().printTree(0));
		return iniCommit.getObjectHash();
	}

	private boolean writeToDeveloperList(String committer, String workingDir) {
		// TODO Auto-generated method stub
		String devListFile= workingDir +"/" +Constants.VCSFOLDER +"/" +Constants.DEVELOPERS_FILE;
		boolean done=false;
		File f=new File(devListFile);
		if(!f.exists())
		{
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(f.exists())
		{
			try 
			{
				BufferedReader br =new BufferedReader(new FileReader(f));
				ArrayList<String> committerList =new ArrayList<String>();
				String line=null;
				while((line=br.readLine())!=null)
				{
					if(!committerList.contains(line))
					{
						committerList.add(line);
					}
				}
				br.close();
				if(!committerList.contains(committer))
				{
					committerList.add(committer);
				}
				BufferedWriter bw=new BufferedWriter(new FileWriter(f));
				if(f.exists())
				{
					int i=0,max=committerList.size();
					while(i<max)
					{
						if(i==0)
						{
							bw.write(committerList.get(i));
							if(i!=max-1)
							{
								bw.newLine();
							}
						}
						else
						{
							bw.append(committerList.get(i));
							if(i!=max-1)
							{
								bw.newLine();
							}
						}
						i++;
					}
				}
				bw.close();
			}
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return done;
	}

	private void inorder(AbstractVCSTree currentTree,
			AbstractVCSTree parentTree, String pathTillNow, String workingDir) {
		// TODO Auto-generated method stub

		ArrayList<AbstractVCSTree> childrenArray = ((VCSTree)currentTree).getImmediateChildren();
		ArrayList<AbstractVCSTree> parentArray = ((VCSTree)parentTree).getImmediateChildren();
		Iterator<AbstractVCSTree> childArrayIterator = childrenArray.iterator();
		Iterator<AbstractVCSTree> parentArrayIterator = parentArray.iterator();

		while (parentArrayIterator.hasNext()) {
			// AbstractVCSTree arg0 = childArrayIterator.next();
			AbstractVCSTree arg0 = parentArrayIterator.next();

			// VCSTree arg1 = (VCSTree) parentArrayIterator.next();

			// directory
			if (arg0.getType() == "tree") {
				// already not present
				VCSTree arg1 = (VCSTree) ((VCSTree) currentTree).getIfExist(
						arg0.getName(), "tree");
				if (arg1 == null) {
					// VCSTree eleAtPath = new
					// VCSTree(arg0.getName(),pathTillNow, workingDir);
					// ((VCSTree)currentTree).addItem(eleAtPath);
					// currentTree = eleAtPath;

					((VCSTree) currentTree).addItem(arg0);
					// currentTree = eleAtPath;

				} else {
					inorder(arg1, arg0, pathTillNow + "/" + arg1.getName(),
							workingDir);
				}

			} else if (arg0.getType() == "blob") // files
			{
				// already not present
				VCSBlob arg1 = (VCSBlob) ((VCSTree) currentTree).getIfExist(
						arg0.getName(), "blob");

				if (arg1 == null) {
					// VCSBlob lastEleAtPath = new VCSBlob(arg0.getName(),
					// pathTillNow, workingDir);
					((VCSTree) currentTree).addItem(arg0);
				}
			}
		}

	}

	public String getCurrentBranchName(String workingDir)
	{
		String head;
		try 
		{
			VCSCommit temp=getHead(workingDir);
			if(temp!=null)
			{
				head=temp.getObjectHash();
				System.out.println("head =" +head);
			}
			else
			{
				head=null;
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			head=null;
		}
		ArrayList<String> branchHeads=new ArrayList<String>();
		String branchesFolder=workingDir+"/" +Constants.VCSFOLDER +"/" +Constants.BRANCH_FOLDER;
		String branchName=null;
		listAllFiles(branchesFolder, branchHeads, branchesFolder);
		int size=branchHeads.size();
		boolean done=false;
		if(head!=null && branchHeads!=null && size>0)
		{
			int i=0;
			while(i<size && done==false)
			{
				File f=new File(branchHeads.get(i));
				if(f.exists())
				{
					BufferedReader br;
					try 
					{
						br = new BufferedReader(new FileReader(f));
					}
					catch (FileNotFoundException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						br=null;
					}
					String line;
					try 
					{
						while((line=br.readLine())!=null)
						{
							if(line.equals(head))
							{
								branchName=f.getName();
								done=true;
							}
						}
					}
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						branchName=null;
					}
				}
				i++;
			}
			if(branchName==null)
			{
				throw new NullPointerException();
			}
		}
		else
		{
			branchName= "master";
		}
		return branchName;
	}
	
	public static String readFileIntoString(String completeFileName) {
		String retVal = null;
		try {
			//System.out.println("complete file name" +completeFileName);
			retVal = new String(Files.readAllBytes(Paths.get(completeFileName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	public VCSCommit getBranchHead(String workingDir,String branchName) throws IOException{
		BufferedReader br = null;
		File branch = new File(workingDir + ".vcs/branches/" + branchName);
		VCSCommit parent = null;
		String sCurrentLine;
		if(branch.exists()){
			br = new BufferedReader(new FileReader(workingDir + ".vcs/branches/" + branchName));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				parent = new VCSCommit(sCurrentLine, workingDir, VCSCommit.IMPORT_TREE);
			}
			br.close();
		}
		return parent;
	}
	
	public boolean writeBranchHead(String workingDir,String commitHash,String branchName) throws IOException{
		File branch = new File(workingDir + ".vcs/branches/"+branchName);
		FileWriter fileWritter = new FileWriter(branch,false);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	    bufferWritter.write(commitHash);
	    bufferWritter.close();
		return true;
	}
	
	boolean mergeBranch(String workingDirectory,String firstBranch,String secondBranch) throws IOException
	{
		boolean retval = false;
		String branchDirectory = workingDirectory + ".vcs/branches";
		System.out.println("inside mergeBranch");
		if(branchExists(workingDirectory, firstBranch) && branchExists(workingDirectory, secondBranch))
		{
			VCSCommit firstCommitObject = getBranchHead(workingDirectory, firstBranch);
			VCSCommit secondCommitObject = getBranchHead(workingDirectory, secondBranch);
			VCSTree firstVCSTree =  firstCommitObject.getTree();
			VCSTree secondVCSTree =  secondCommitObject.getTree();
			VCSTree mergedVCSTree =  new VCSTree(firstVCSTree.getName(), firstVCSTree.getType(), workingDirectory);
			
			if(firstVCSTree == null && secondVCSTree == null)
			{
				System.out.println("1th case");
				return false;
			}
			else if(firstVCSTree == null && secondVCSTree != null)
			{
				System.out.println("2th case");
				mergedVCSTree = secondVCSTree;
			}
			else if(firstVCSTree != null && secondVCSTree == null)
			{
				System.out.println("3th case");
				mergedVCSTree = firstVCSTree;
			}
			else
			{
				System.out.println("4th case");
				
				File branchOneTmpDir = new File(firstVCSTree.getWorkingDirectory()+".vcs/tmp/"+"branchOne/");
				branchOneTmpDir.mkdirs();
				File branchTwoTmpDir = new File(secondVCSTree.getWorkingDirectory()+".vcs/tmp/"+"branchTwo/");
				branchTwoTmpDir.mkdirs();
				File ancestorTmpDir = new File(secondVCSTree.getWorkingDirectory()+".vcs/tmp/"+"ancestor/");
				ancestorTmpDir.mkdirs();
				VCSTree commonAncestor = getCommonAncestor(firstVCSTree,secondVCSTree);
				mergeTree(firstVCSTree,secondVCSTree,mergedVCSTree,commonAncestor);
			}
			//write mergedVCSTree to disk
			mergedVCSTree.writeOriginalToDisk();
		}
		return retval;
	}
	private VCSTree getCommonAncestor(VCSTree firstVCSTree, VCSTree secondVCSTree) {
		// TODO Auto-generated method stub
		
		
		int firstLength = getLength(firstVCSTree);
		int secondLength = getLength(secondVCSTree);
		int diff;
		if(firstLength > secondLength)
		{
			diff= firstLength - secondLength;
		}
		else if(firstLength < secondLength)
		{
			diff = secondLength - firstLength;
		}
		else
		{
			diff = 0;
		}
		return null;
	}
	private int getLength(VCSTree VCSTreeObj) {
		// TODO Auto-generated method stub
		return 0;
	}
	void mergeTree(VCSTree firstVCSTree,VCSTree secondVCSTree,VCSTree mergedVCSTree, VCSTree commonAncestor)
	{
		ArrayList<AbstractVCSTree> firstVCSTreeChildren = firstVCSTree.getImmediateChildren();
		ArrayList<AbstractVCSTree> secondVCSTreeChildren = secondVCSTree.getImmediateChildren();
		int firstVCSTreeChildrenLength = firstVCSTreeChildren.size();
		
		int firstCounter=0;
		int secondCounter=0;
		Iterator<AbstractVCSTree> firstVCSTreeIterator = firstVCSTreeChildren.iterator();
		while(firstVCSTreeIterator.hasNext())
		{
			AbstractVCSTree firstTmpObj = firstVCSTreeIterator.next();
			AbstractVCSTree secondTmpObj = secondVCSTree.getIfExist(firstTmpObj.getName(), firstTmpObj.getType());
			//common trees(directories)
			if(secondTmpObj!=null && secondTmpObj.getType().equals("tree"))
			{
				VCSTree mergedVCSTreeChild = new VCSTree(firstTmpObj.getName(),firstTmpObj.getPath(),firstTmpObj.getWorkingDirectory());
				mergedVCSTree.addItem(mergedVCSTreeChild);
				mergeTree((VCSTree)firstTmpObj,(VCSTree)secondTmpObj,mergedVCSTreeChild,commonAncestor);
				firstVCSTreeChildren.remove(firstTmpObj);
				secondVCSTreeChildren.remove(secondTmpObj);
			}
			//common files
			else if(secondTmpObj!=null && secondTmpObj.getType().equals("blob"))
			{
				//same hash => no change
				if(firstTmpObj.getObjectHash().equals(secondTmpObj.getObjectHash()))
				{
					mergedVCSTree.addItem(firstTmpObj);
				}
				//changed files
				else
				{
					System.out.println("Rao's merge entered");
					//call rao's function to merge both files
					((VCSBlob)firstTmpObj).writeOriginalToTempDir(firstTmpObj.getWorkingDirectory() + ".vcs/tmp/branchOne/" + firstTmpObj.getName());
					((VCSBlob)secondTmpObj).writeOriginalToTempDir(secondTmpObj.getWorkingDirectory() + ".vcs/tmp/branchTwo/" + secondTmpObj.getName());
					//((VCSBlob)firstTmpObj).writeOriginalToTempDir(firstTmpObj.getWorkingDirectory() + firstTmpObj.getRelativePath());
					
					
					String file1Contents = readFileIntoString(firstTmpObj.getWorkingDirectory() + ".vcs/tmp/branchOne/" + firstTmpObj.getName());
					String file2Contents = readFileIntoString(secondTmpObj.getWorkingDirectory() + ".vcs/tmp/branchTwo/" + secondTmpObj.getName());
					
					//String completeFileName = searchFileInTree(commonAncestor,firstTmpObj.getName());
					AbstractVCSTree completeFileNameObj = commonAncestor.findTreeIfExist(firstTmpObj.getName(), 0);
					((VCSBlob)completeFileNameObj).writeOriginalToTempDir(firstTmpObj.getWorkingDirectory() + ".vcs/tmp/ancestor/" + firstTmpObj.getName());
					String file3Contents = readFileIntoString(firstTmpObj.getWorkingDirectory() + ".vcs/tmp/ancestor/" + firstTmpObj.getName());
					
					Diff diff = new Diff();
					MergeResult mr=diff.merge(file3Contents, file1Contents, file2Contents, null, false);
					
					//write this to blob
					//File mergedFile = new File(firstTmpObj.getPath());
					
					VCSBlob mergedFile = new VCSBlob(firstTmpObj.getName(), firstTmpObj.getPath(), firstTmpObj.getWorkingDirectory());
					//add the contents of "mr.getDefaultMergedResult()" to 'mergedFile'
					mergedVCSTree.addItem(mergedFile);
					//mr.getDefaultMergedResult();
				}
			}
			//non commmon files and directories
			else
			{
				mergedVCSTree.addItem(firstTmpObj);
			}
		}
		Iterator<AbstractVCSTree> secondVCSTreeIterator = secondVCSTreeChildren.iterator();
		while(secondVCSTreeIterator.hasNext())
		{
			AbstractVCSTree secondTmpObj = secondVCSTreeIterator.next();
			mergedVCSTree.addItem(secondTmpObj);
		}
	}


	boolean branchExists(String workingDirectory,String branchName)
	{
		boolean retval = false;
		String branchDir = workingDirectory + ".vcs/branches";
		File f = new File(branchDir);
	    String[] files  = f.list();
	    int filesArraySize = files.length;
	    System.out.println("files size : "+filesArraySize);
	    System.out.println(files[0]);
	    System.out.println(files[1]);
	    int flag = 1;
	    for(int i=0;i<filesArraySize && !retval;i++)
	    {
	    	if(branchName.equals(files[i]))
	    	{
	    		retval = true;
	    	}
	    }
		return retval;
	}

	boolean switchBranch(String nameOfBranch,String workingDirectory) throws IOException
	{
		boolean retval = true;
		//String workingDirectory = "/home/rounak/final year project/VCS v1.5.0/VCSDebug/";
		String branchDir = workingDirectory + ".vcs/branches";
		File f = new File(branchDir);
	    File[] files  = f.listFiles();
	    int filesArraySize = files.length;
	    int flag = 1;
	    for(int i=0;i<filesArraySize && flag==1;i++)
	    {
	    	if(nameOfBranch.equals(files[i]))
	    	{
	    		flag = 0;
	    	}
	    }
	    if(flag == 0)
	    {
	    	System.out.println("No such branch exists !");
	    }
	    else
	    {
	    	VCSCommit VCSCommitObj = getBranchHead(workingDirectory, nameOfBranch);
	    	writeHead(workingDirectory, VCSCommitObj.getObjectHash());
	    	VCSCommitObj.getTree().writeOriginalToDisk();
	    }

		return retval;
	}
	boolean createBranch(String nameOfBranch,String commitHash,String workingDirectory) throws IOException
	{
		//String workingDirectory = "/home/rounak/final year project/VCS v1.5.0/VCSDebug/";
		//String branchDir = "/home/rounak/final year project/VCS v1.5.0/VCSDebug/.vcs/branches";
		String branchDir = workingDirectory + ".vcs/branches";
		
		System.out.println("create branch dir "+branchDir);
		
		File f = new File(branchDir);
	    File[] files  = f.listFiles();
	    int flag = 1;
	    boolean retval = false;
		
	    
	    File branchFile = null;
	    //if(files.length==0 && nameOfBranch.equals("Master"))
	    int filesArraySize = files.length;
	    System.out.println("number of branches : "+filesArraySize);
	    if(filesArraySize>=1 && (nameOfBranch.equals("Master") || nameOfBranch.equals("master")))
	    {
	    	flag = 0;
	    }
	    for(int i=0;i<filesArraySize && flag==1;i++)
	    {
	    	if(nameOfBranch.equals(files[i]))
	    	{
	    		flag = 0;
	    	}
	    }
	    if(flag==1)
	    {
	    	branchFile = new File(branchDir + "/" +nameOfBranch);
	    	
	    	FileWriter fw = new FileWriter(branchFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(commitHash);
			bw.close();
			retval = true;
	    }
	    if(retval)
	    {
	    	writeHead(workingDirectory, commitHash);
	    }
	    return retval;
	}
	
	private void listAllFiles(String directoryName, ArrayList<String> files,String initialWorkingDir) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) 
	    {
	        if (file.isFile()) 
	        {
	        	//System.out.println("dirName " +directoryName +" iniworkingDir "+initialWorkingDir);
	        	String tempAbsPath=file.getAbsolutePath();
	        	if(tempAbsPath.contains("\\"))
	        	{
	        		tempAbsPath=tempAbsPath.replace("\\", "/");
	        	}
	        	//System.out.println("tempabspath " + tempAbsPath);
	        	String temp=tempAbsPath.replace(initialWorkingDir,"");
	        	//System.out.println("temp is "+temp);
				files.add(temp);
	        }
	        else if (file.isDirectory() && !(file.getName().startsWith("."))) 
	        {
	        	listAllFiles(file.getAbsolutePath(), files,initialWorkingDir);
	        }
	    }
	}
	public boolean writeCloneConfig(String workingDir, String repoUrl) throws IOException
	{
		File config = new File(workingDir + Constants.VCSFOLDER + "/config");
		boolean status;
			
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(config, true)))) {
		    out.println("[remote 'origin']\n\turl = " + repoUrl);
		    //[remote 'origin'] url = repoUrl or http://127.0.0.1:8080/VCSD_1_1.vcs
		    status = true;
		}
		catch (IOException e) {
			status = false;
			VCSLogger.infoLogToCmd("DATA WRITES TO CONFIG FILE FAILED!");
		}
		return status;
	}	
	
	public String clone(String repoUrl,String workDir){
		URLConnection conn;
		try {
			conn = new URL(repoUrl + "?REQUEST=CLONE").openConnection();
			//conn.setRequestProperty("Accept-Charset", "UTF-8"); *Not required
			conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			conn.setRequestProperty("Accept","*/*");
			
			//Receives Response from server (Check SimpleWebServer.java and stores in response InputStream)
			//InputStream response = conn.getInputStream();
			
			int status = ((HttpURLConnection) conn).getResponseCode();
			
			VCSLogger.infoLogToCmd("STATUS RECEIVED: " + status);
			
			/*FileOutputStream fos = new FileOutputStream(file);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = response.read(bytes)) >= 0) {
				fos.write(bytes, 0, length);
			}
			fos.close();*/
			
			/** 
			 * NanoHttpd serve() is automatically called 
			 * and the response is stored in the InputStream variable response.
			 * 
			 * 1.) Now Unzip the .zip file received from the server and  
			 * generate a local directory for the same with an updated config file. and extra updates (?Check)
			 * 2.) On success, we add the remote repoUrl in config file with handle `origin`
			 * config:
			 * [remote 'origin'] url = repoUrl or http://127.0.0.1:8080/VCSD_1_1.vcs
			 */
			/*
			boolean writeStatus = writeCloneConfig(workDir, repoUrl);
			if(writeStatus){
				VCSLogger.infoLogToCmd("CONFIG WRITE SUCCESS.");
			}
			else {
				VCSLogger.infoLogToCmd("CONFIG WRITE FAILURE.");
			}
			*/
			String userHomeDir = System.getProperty("user.home");
			
			String fileNameWithExtn = repoUrl.substring( repoUrl.lastIndexOf('/')+1, repoUrl.length() );
			String fileNameWithoutExtn = fileNameWithExtn.substring(0, fileNameWithExtn.lastIndexOf('.'));			

			String fileName = userHomeDir + "/" + fileNameWithoutExtn + ".zip";
			
			VCSLogger.infoLogToCmd("Unzipping...\nFILE:  " + fileName);
			
			try {
				ZipFile zipFile = new ZipFile(fileName);
				Enumeration<?> enu = zipFile.entries();
				while (enu.hasMoreElements()) {
					ZipEntry zipEntry = (ZipEntry) enu.nextElement();

					String name = zipEntry.getName();
					long size = zipEntry.getSize();
					long compressedSize = zipEntry.getCompressedSize();
					System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", 
							name, size, compressedSize);

					File file = new File(name);
					if (name.endsWith("/")) {
						file.mkdirs();
						continue;
					}

					File parent = file.getParentFile();
					if (parent != null) {
						parent.mkdirs();
					}

					InputStream is = zipFile.getInputStream(zipEntry);
					FileOutputStream fos = new FileOutputStream(file);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = is.read(bytes)) >= 0) {
						fos.write(bytes, 0, length);
					}
					is.close();
					fos.close();

				}
				zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/* Code to see the content received from the server side.
			FileOutputStream outStream = new FileOutputStream(new File(workDir));
			
			byte buffer[] = new byte[8192];
			while(response.read(buffer) > 0){
				outStream.write(buffer);
			}
			outStream.close();	
			response.close();
			*/
			
		} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
