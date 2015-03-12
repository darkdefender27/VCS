package vcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import logger.VCSLogger;

import objects.AbstractVCSTree;
import objects.VCSCommit;


public class VCS {

	public static void main(String[] args){
		//workDir is absolute
		String cmdArgs = "init /home/warrior/Downloads/VCSDebug/";
		//String cmdArgs = "add /home/warrior/Downloads/VCSDebug/ web/index.jsp";
		//String cmdArgs = "commit /home/warrior/Downloads/VCSDebug/ warrior commitMessage";
		args = cmdArgs.split(" ");
		int argLength = args.length;
		if( argLength >= 1){
			Operations ops = new Operations();
			if(args[0].equals("init") && argLength == 2){
				//init workDir
				if(!ops.initRepository(args[1]))
					VCSLogger.infoLogToCmd("Unable to initialise repository");
			}
			if(args[0].equals("add") && argLength == 3){
				//add workDir stagedFile
				String workingDir = args[1];
				String stagedFile = args[2];
				try{
		    		File stagingFile =new File(workingDir + ".vcs/info/staged");
		    		boolean exists = true;
		    		if(!stagingFile.exists()){
		    			stagingFile.createNewFile();
		    			exists = false;
		    		}
		    		//true = append file
		    		FileWriter fileWritter = new FileWriter(stagingFile,true);
		    		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		    	    if(exists)bufferWritter.write("\n");
		    	    bufferWritter.write(stagedFile);
		    	    bufferWritter.close();
		 
		    	    VCSLogger.infoLogToCmd("Added to staging area");
		    	}catch(IOException e){
		    		VCSLogger.errorLogToCmd("VCS#Main#add", e.toString());
		    	}
			}
			if(args[0].equals("checkout") && argLength == 3){
				//checkout workDir hash
				String workingDir = args[1];
				String commitHash = args[2];
				VCSCommit commit = new VCSCommit(commitHash, workingDir, true);
				boolean status = true;
				Iterator<AbstractVCSTree> it = commit.getTree().getImmediateChildren().listIterator();
				while(it.hasNext() && status){
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
						while ((sCurrentLine = br.readLine()) != null) {
							stagedContent.append(sCurrentLine);
						}
						String[] stagedFiles = (stagedContent.toString()).split("\n");
						if(br != null) br.close();
						
						File head = new File(workingDir + ".vcs/refs/heads/master");
						VCSCommit parent = null;
						if(head.exists()){
							br = new BufferedReader(new FileReader(workingDir + ".vcs/refs/heads/master"));
							while ((sCurrentLine = br.readLine()) != null) {
							}
							br.close();
							//System.out.println("Parent" + sCurrentLine);
							parent = new VCSCommit(sCurrentLine, workingDir, true);
						}
						String commitHash = ops.commit(stagedFiles, parent, message, user, user, workingDir);
						
						FileWriter fileWritter = new FileWriter(head,false);
			    		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			    	    bufferWritter.write(commitHash);
			    	    bufferWritter.close();
			    	    
						stagingFile.delete();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
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