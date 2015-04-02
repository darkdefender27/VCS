package vcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
		//workDir is absolute
		//String cmdArgs = "init /home/shubham/VCSTemp/Garbage/vcsd1/";
		//String cmdArgs = "add /home/ambarish/Desktop/vcsdebug/ *";
		//String cmdArgs = "commit /home/ambarish/Desktop/vcsdebug/ ambarish.v.rao@gmail.com initial";
		
		//String cmdArgs = "checkout /home/warrior/Downloads/VCSDebug/ e03c4cbb9a9ecc3e67afc039b3bad4f87048b395b37d5711984041663c2a4b";
		//args = cmdArgs.split(" ");
		int argLength = args.length;
		if( argLength >= 1){
			Operations ops = new Operations();
			
			//~~ Network Operations.
			if (args[0].equals("start-server") && argLength == 3){
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
			
		
		//ops.initRepository("./");
		//String[] stagedFiles = {"root/new1/3.c","root/new1/new4/5.c","root/new2/1.c","root/new2/2.c"};
		
		//ops.commit(stagedFiles, null,"initial commit", "", "", "./");
//		
//		VCSBlob blob1 = new VCSBlob("hello.c", "./root/hello.c", "./");
//		//blob1.writeObjectToDisk();
//		
//		VCSBlob blob2 = new VCSBlob("new.c", "./root/new/new.c", "./");
////		blob2.writeObjectToDisk();
//		
//		VCSTree tree1 = new VCSTree("new", "./root/new/", "./");
//		tree1.addItem(blob2);
//		System.out.println(tree1.generateTreeHash());
////		tree1.writeObjectToDisk();
////		
//		VCSTree tree2 = new VCSTree("root", "./root/", "./");
//		tree2.addItem(tree1);
//		tree2.addItem(blob1);
//		System.out.println(tree2.generateTreeHash());
//		//tree2.writeTreeToDisk();
//		System.out.print(tree2.printTree(0));
//		
		
		//VCSBlob blob = new VCSBlob("7dc736115fddc4a178c7f38061d739d8ad36bbceccea597cdb3a9f17557c", "./", "./tree", "tree");
		//System.out.println(blob.writeOriginalToDisk());
//	
//		VCSTree tree = new VCSTree("7dc736115fddc4a178c7f38061d739d8ad36bbceccea597cdb3a9f17557c", "./", "./root", "root");
		//tree.writeOriginalToDisk();
//		System.out.print(tree.printTree(0));
		//VCSCommit commit = new VCSCommit("./", null, tree2, "ini Commit", "prashant", "prashnayt");
//		commit.writeObjectToDisk();
//		System.out.println(commit.getObjectHash());
		
		//VCSCommit co = new VCSCommit(objectHash, workingDirectory)
	}
	
}
