package vcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import logger.VCSLogger;
import objects.AbstractVCSTree;
import objects.VCSBlob;
import objects.VCSCommit;
import objects.VCSObject;
import network.NetworkOps;
import network.SimpleWebServer;

public class VCS {
	
	private static String emailID=null;
	///compare hashes for changed files
	private static void listAllFiles(String directoryName, ArrayList<String> files,String initialWorkingDir) 
	{
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
	
	
	public static String getUserName()
	{
		String retVal=null;
		String homeDir=System.getProperty("user.home").replace("\\", "/");
		File f=new File(homeDir+"/" +Constants.CONFIG_FILE);
		String line;
		if(f.exists())
		{
			try 
			{
				BufferedReader br=new BufferedReader(new FileReader(f));
				boolean done=false;
				try 
				{
					while(done ==false && (retVal=br.readLine())!=null)
					{
						if(retVal.contains("[user]"))
						{
							retVal=br.readLine();
							if(retVal.contains("="))
							{
								retVal=retVal.split("=")[1];
								retVal=retVal.replace(" ", "");
								line=br.readLine();
								if(line.contains("email"))
								{
									line=line.split("=")[1];
									line=line.replace(" ", "");
									emailID=line;
								}
								done=true;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try 
			{
				boolean created=f.createNewFile();
				if(created)
				{
					BufferedWriter bw=new BufferedWriter(new FileWriter(f));
					bw.append("[user]");
					bw.append("\n");
					bw.append("\t");
					bw.append("name=");
					bw.append("newUser");
					bw.append("\n");
					bw.append("\t");
					bw.append("email=");
					bw.append("newUser@newUser.com");
					bw.close();
					retVal="newUser";
				}
				
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	public static void main(String[] args)
	{
		String userName=null;
		//place your cmdArgs here if required
		
		String cmdArgs = "branch /home/rounak/final#year#project/VCS#v1.5.0/VCSDebug/";
		//String cmdArgs = "log /home/rounak/final#year#project/VCS#v1.5.0/VCSDebug/";
		//String cmdArgs = "switch /home/rounak/final#year#project/VCS#v1.5.0/VCSDebug/ branch branch1";
		args = cmdArgs.split(" ");
		//end of cmdArgs
		userName=getUserName();
		args[1] = replaceHashWithSpace(args[1]);
		
		boolean flag = false;
		int argLength = args.length;
		if( argLength >= 1){
			
			//if(userName!=null)
			{
				Operations ops = new Operations();
				
				//~~ Network Operations.
				if (args[0].equals("start-server") && argLength == 3)
				{
					NetworkOps netOps = new NetworkOps();
					SimpleWebServer server = new SimpleWebServer(args[1],Integer.parseInt(args[2]),netOps);
					try {
						server.start();
						System.out.println("Server started");
					} catch (IOException e) {
						e.printStackTrace();
					}
					Scanner scanner = new Scanner(System.in);
					if(scanner.nextInt() == 0) server.stop();
					System.out.println("stopped");
					scanner.close(); // Scanner was not closed.
				}
				if (args[0].equals("clone") && argLength == 3){
					//clone http://ip:port/repoName.vcs /home/../somePathOnLocalMachine/
					ops.clone(args[1],args[2]);
				}
				if (args[0].equals("pull") && argLength == 2){
					//pull origin i.e. "vcs pull remote_handle_name"
					ops.pull(args[1]);
				}
				if (args[0].equals("push") && argLength == 3){
					//push origin master i.e. "vcs push remote_handle_name target_branch_name"
					try {
						ops.push(args[1], args[2]);
					} 
					catch (IOException e) {
						VCSLogger.debugLogToCmd("NETWORK:PUSH", "PUSH FAILED AT START: " + e);
					}
				}
				
				//~~
			
				if(args[0].equals("init") && argLength == 2){
					//init workDir
					if(!ops.initRepository(args[1]))
					{
						VCSLogger.infoLogToCmd("Unable to initialise repository");
					}
				}
				if(args[0].equals("add") && argLength == 3)
				{
					//add workDir stagedFile
					String workingDir = replaceHashWithSpace(args[1]);
					String stagedFile = args[2];
					try{
						if(stagedFile.contains("*"))
						{
							ArrayList<String> allFiles=new ArrayList<>();
							
							String newWorkingDir=workingDir+stagedFile.replace("*", "");
							
							listAllFiles(newWorkingDir,allFiles,workingDir);
							int i=0;
							File stagingFile =new File(workingDir + ".vcs/info/staged");
				    		boolean exists = true;
				    		if(!stagingFile.exists())
				    		{
				    			stagingFile.createNewFile();
				    		}
				    		FileWriter fileWritter = new FileWriter(stagingFile,true);
				    		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
							while(i<allFiles.size())
							{
								if(exists)
								{
									if(i!=0)
									{
										bufferWritter.write("\n");
									}
								}
								//System.out.println("checking for abs or rel "+allFiles.get(i));
					    	    bufferWritter.write(allFiles.get(i));
					    	    VCSLogger.infoLogToCmd(allFiles.get(i)+" added to staging area");
								i++;
							}
							exists = false;
							bufferWritter.close();
						}
						else
						{
				    		File stagingFile =new File(workingDir + ".vcs/info/staged");
				    		boolean exists = true;
				    		if(!stagingFile.exists())
				    		{
				    			stagingFile.createNewFile();
				    			exists = false;
				    		}
				    		//true = append file
				    		FileWriter fileWritter = new FileWriter(stagingFile,true);
				    		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				    	    if(exists)bufferWritter.write("\n");
				    	    bufferWritter.write(stagedFile);
				    	    bufferWritter.close();
				 
				    	    VCSLogger.infoLogToCmd(stagedFile+" added to staging area");
						}
			    	}catch(IOException e){
			    		e.printStackTrace();
			    		//VCSLogger.errorLogToCmd("VCS#Main#add", e.toString());
			    	}
				}
				if(args[0].equals("checkout")){
					//checkout workDir -b name
					//checkout workDir -f relativePath(src/1.txt) commitHash
					VCSCommit commit = null;
					boolean status = false;
					if(args[2].equals("-b") && argLength == 4){
						//branch
						try {
							commit = ops.getBranchHead(args[1], args[3], VCSCommit.IMPORT_TREE);
							Iterator<AbstractVCSTree> it = commit.getTree().getImmediateChildren().listIterator();
							while(it.hasNext())
							{
								status = (it.next()).writeOriginalToDisk();
								if(!status) break;
							}
						} catch (IOException e) {
							e.printStackTrace();
							//VCSLogger.errorLogToCmd("VCS#Main#checkout", e.toString());
						}
					}else if(args[2].equals("-f") && argLength == 5){
						//file
						args[3] = args[3].replaceFirst("^/+", "");
						commit = new VCSCommit(args[4], args[1], VCSCommit.IMPORT_TREE);
						AbstractVCSTree file = commit.getTree().findTreeIfExist(args[3], 0);
						status = ((VCSBlob)file).writeOriginalToDisk();
					}else{
						VCSLogger.infoLogToCmd("No such option exist");
					}
					if(status) VCSLogger.infoLogToCmd("Successfully checked out");
				}
				if(args[0].equals("status") && argLength == 2){
					//status workDir
					ops.vcsStatus(args[1]);
				}
				if(args[0].equals("commit") && argLength == 3){
					//commit workDir message
					String workingDir = args[1];
					String message = args[2];
					BufferedReader br = null;
					File stagingFile = new File(workingDir + ".vcs/info/staged");
					if(stagingFile.exists()){
						try {
							String sCurrentLine;
							StringBuilder stagedContent = new StringBuilder();
							br = new BufferedReader(new FileReader(workingDir + ".vcs/info/staged"));
							while ((sCurrentLine = br.readLine()) != null) 
							{
								VCSLogger.debugLogToCmd("VCS#Main#Commit#Filename", sCurrentLine);
								stagedContent.append(sCurrentLine + "\n");
							}
							stagedContent.deleteCharAt(stagedContent.length()-1);
							String[] stagedFiles = (stagedContent.toString()).split("\n");
							if(br != null) br.close();
							
							VCSCommit parent =  ops.getHead(workingDir);
							
							//userName="darkDefender";
							
							String commitHash = ops.commit(stagedFiles, parent, message, userName, userName, workingDir);
				    	    ops.writeHead(workingDir, commitHash);
							stagingFile.delete();
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
							//VCSLogger.errorLogToCmd("VCS#Main#commit", e.toString());
						}
						finally 
						{
							try 
							{
								if (br != null)br.close();
							} catch (IOException ex) {
								ex.printStackTrace();
								//VCSLogger.errorLogToCmd("VCS#Main#commit", ex.toString());
							}
						}
					}
					else{
						VCSLogger.infoLogToCmd("No files staged to commit");
					}
				}
				if(args[0].equals("create") && args[2].equals("branch") && argLength == 4)
				{
					//create workDir branch name
					String branchName = args[3];
					String workingDir=replaceHashWithSpace(args[1]);
					try {
						if(workingDir.contains("\\"))
						{
							workingDir=workingDir.replace("\\", "/");
							System.out.println("create section " +workingDir);
						}
						System.out.println("create section " +workingDir);
						flag = ops.createBranch(branchName,workingDir);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(flag)
					{
						System.out.println("Branch created successfully");
					}
					
				}
				if(args[0].equals("switch") && args[2].equals("branch") && argLength == 4)
				{
					//switch workDir branch name
					String branchName = args[3];
					try {
						String workDir = replaceHashWithSpace(args[1]);
						flag = ops.switchBranch(branchName,workDir);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(flag)
					{
						System.out.println("Branch switched successfully");
					}
				}
				if(args[0].equals("branch") && argLength == 2)
				{
					//branch workDir
					try {
						ops.branch(args[1]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(args[0].equals("clean") && argLength == 2)
				{
					//clean workingDir
					ops.clean(args[1]);
				}
				if(args[0].equals("merge") && args[1].equals("branch") && argLength == 5)
				{
					//merge branch workDir b1 b2
					String firstBranchName = args[3];
					String secondBranchName = args[4];
					try {
						String workDir = replaceHashWithSpace(args[2]);
						if(ops!=null)
						{
							System.out.println("ops not null");
							VCSCommit firstBranchHead = ops.getBranchHead(workDir, firstBranchName,VCSCommit.IMPORT_TREE);
							VCSCommit secondBranchHead = ops.getBranchHead(workDir, secondBranchName,VCSCommit.IMPORT_TREE);
							if(firstBranchHead !=null && secondBranchHead !=null)
							{
								System.out.println("preparing for merge");
								flag = ops.mergeBranch(workDir, firstBranchHead.getObjectHash(),secondBranchHead.getObjectHash(),null);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("inside merge");
					if(flag)
					{
						System.out.println("Branch merged successfully");
					}
					
				}if(args[0].equals("log") && argLength == 2){
					//log workdir
					ops.vcsCommitLog(args[1]);
				}
				if(args[0].equals("diff") &&  argLength==3)
				{
					String workingDir=args[1];
					String fileName=args[2];
					try 
					{
						VCSCommit parent=ops.getHead(workingDir);
						ops.doVCSDiff(parent, fileName, workingDir);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(args[0].equals("show") && argLength==3)
				{
					String workingDir=args[1];
					String id=args[2];
					String retVal=VCSObject.returnObject(id,workingDir);
					VCSLogger.infoLogToCmd(retVal);
					
				}
				if(args[0].equals("config") && argLength==6)
				{
					String workingDir=args[1];
					String name=args[5];
					String homeDir=System.getProperty("user.home").replace("\\", "/");
					File f=new File(homeDir+"/" +Constants.CONFIG_FILE);
					BufferedWriter bw;
					try 
					{
						bw = new BufferedWriter(new FileWriter(f));
						bw.write("[user]");
						bw.append("\n");
						bw.append("\t");
						bw.append("name=");
						if(args[4].equals("name"))
						{
							bw.append(name);
						}
						else
						{
							bw.append(userName);
						}
						bw.append("\n");
						bw.append("\t");
						bw.append("email=");
						if(args[4].equals("email"))
						{
							bw.append(name);
						}
						else
						{
							bw.append(emailID);
						}
						bw.close();
						VCSLogger.infoLogToCmd("Operation completed successfully");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	public static String replaceHashWithSpace(String arg)
	{
		char[] tmp = arg.toCharArray();
		int stringLength = arg.length();
		for(int i=0;i<stringLength;i++)
		{
			if(tmp[i]=='#')
			{
				tmp[i]=' ';
			}
		}
		String retval = new String(tmp);
		return retval;
	}

	/*
	 * //workDir is absolute
		String cmdArgs = "init /home/ambarish/Desktop/vcsdebug/";
		//String cmdArgs = "add /home/ambarish/Desktop/vcsdebug/ *";
		//String cmdArgs = "commit /home/ambarish/Desktop/vcsdebug/ ambarish.v.rao@gmail.com initial";
	 */
}
