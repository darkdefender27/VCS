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
import objects.VCSCommit;
import network.NetworkOps;
import network.SimpleWebServer;

public class VCS {
	
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
		return retVal;
	}

	public static void main(String[] args)
	{
		String userName=null;
		
		//String cmdArgs = "init C:/Users/Ambarish/Desktop/vcsdebug/";
		//String cmdArgs = "add C:/Users/Ambarish/Desktop/vcsdebug/ *";
		String cmdArgs = "commit C:/Users/Ambarish/Desktop/vcsdebug/ master_4";
		//String cmdArgs = "create C:/Users/Ambarish/Desktop/vcsdebug/ branch b1 a46a7d06dbbcdef2d9ab10365e634edc9a629a7c593df712017ca31c31e33";
		//String cmdArgs ="switch C:/Users/Ambarish/Desktop/vcsdebug/ branch master";
		args = cmdArgs.split(" ");
		userName=getUserName();
		args[1] = replaceHashWithSpace(args[1]);
		//args = cmdArgs.split(" ");
		//args[1] = replaceHashWithSpace(args[1]);
		boolean flag = false;
		int argLength = args.length;
		if( argLength >= 1){
			
			if(userName!=null)
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
					ops.clone(args[1],args[2]);
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
					String workingDir = args[1];
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
			    		VCSLogger.errorLogToCmd("VCS#Main#add", e.toString());
			    	}
				}
				if(args[0].equals("checkout") && argLength == 3){
					//checkout workDir hash
					String workingDir = args[1];
					String commitHash = args[2];
					VCSCommit commit = new VCSCommit(commitHash, workingDir, VCSCommit.IMPORT_TREE);
					boolean status = true;
					Iterator<AbstractVCSTree> it = commit.getTree().getImmediateChildren().listIterator();
					//VCSLogger.debugLogToCmd("VCS#MAIN#checkout",commit.getTree().printTree(0));
					//VCSLogger.debugLogToCmd("VCS#MAIN#checkout", "Tree Printed");
					while(it.hasNext())
					{
						status = (it.next()).writeOriginalToDisk();
						if(!status) break;
					}
					if(status) VCSLogger.infoLogToCmd("Successfully checked out");
				}
				if(args[0].equals("commit") && argLength == 3){
					//commit workDir user message
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
							
							userName="darkDefender";
							
							String commitHash = ops.commit(stagedFiles, parent, message, userName, userName, workingDir);
				    	    ops.writeHead(workingDir, commitHash);
							stagingFile.delete();
						} 
						catch (IOException e) 
						{
							VCSLogger.errorLogToCmd("VCS#Main#commit", e.toString());
						}
						finally 
						{
							try 
							{
								if (br != null)br.close();
							} catch (IOException ex) {
								//ex.printStackTrace();
								VCSLogger.errorLogToCmd("VCS#Main#commit", ex.toString());
							}
						}
					}
					else{
						VCSLogger.infoLogToCmd("No files staged to commit");
					}
				}
				if(args[0].equals("create") && args[2].equals("branch") && argLength == 5)
				{
					String branchName = args[3];
					String commitHash = args[4];
					String workingDir=args[1];
					try {
						if(workingDir.contains("\\"))
						{
							workingDir=workingDir.replace("\\", "/");
							System.out.println("create section " +workingDir);
						}
						System.out.println("create section " +workingDir);
						flag = ops.createBranch(branchName,commitHash,workingDir);
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
					String branchName = args[3];
					try {
						String workDir = args[1];
						flag = ops.switchBranch(branchName,workDir);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(flag)
					{
						System.out.println("Branch switched successfully");
					}
				}
				if(args[0].equals("merge") && args[1].equals("branch") && argLength == 4)
				{
					String firstBranchName = args[2];
					String secondBranchName = args[3];
					try {
						String workDir = args[1];
						flag = ops.mergeBranch(workDir, firstBranchName,secondBranchName);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("inside merge");
					if(flag)
					{
						System.out.println("Branch merged successfully");
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
