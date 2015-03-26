package vcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import logger.VCSLogger;
import objects.AbstractVCSTree;
import objects.VCSCommit;


public class VCS {
	
	///compare hashes for changed files
	private static void listAllFiles(String directoryName, ArrayList<String> files,String initialWorkingDir) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) 
	    {
	        if (file.isFile()) 
	        {
	        	String tempAbsPath=file.getAbsolutePath();
				files.add(tempAbsPath.replace(initialWorkingDir,""));
	        }
	        else if (file.isDirectory() && !(file.getName().startsWith("."))) 
	        {
	        	listAllFiles(file.getAbsolutePath(), files,initialWorkingDir);
	        }
	    }
	}

	public static void main(String[] args)
	{
		args = cmdArgs.split(" ");
		int argLength = args.length;
		if( argLength >= 1){
			Operations ops = new Operations();
			if(args[0].equals("init") && argLength == 2){
				//init workDir
				if(!ops.initRepository(args[1]))
				{
					VCSLogger.infoLogToCmd("Unable to initialise repository");
				}
			}
			if(args[0].equals("add") && argLength == 3){
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
				while(it.hasNext() && status)
				{
					status = (it.next()).writeOriginalToDisk();
				}
				if(status) VCSLogger.infoLogToCmd("Successfully checked out");
			}
			if(args[0].equals("commit") && argLength == 4){
				//commit workDir user message
				String workingDir = args[1];
				String user = args[2];
				String message = args[3];
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
						String commitHash = ops.commit(stagedFiles, parent, message, user, user, workingDir);
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
		}
	}

	/*
	 * //workDir is absolute
		String cmdArgs = "init /home/ambarish/Desktop/vcsdebug/";
		//String cmdArgs = "add /home/ambarish/Desktop/vcsdebug/ *";
		//String cmdArgs = "commit /home/ambarish/Desktop/vcsdebug/ ambarish.v.rao@gmail.com initial";
	 */
}
