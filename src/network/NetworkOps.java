package network;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import vcs.Constants;
import logger.VCSLogger;

public class NetworkOps {
	
	
    public File CloneRepository(String repoName) {
    	// repoName = abcd.vcs
    	String userHomeDir = System.getProperty("user.home");
    	
    	//String OUTPUT_ZIP_FILE = userHomeDir + "/" + party[0];

    	//MAP the uri's to Absolute Path (repoName [abcd.vcs] -> absolutePath of root directory)
    	String SOURCE_FOLDER = null;
    	try {

    		String fileName = userHomeDir + "/repoListHolder.txt";
    		List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
    		
        	for (String line : lines) {
        		String parts[] = line.split(" ");
        	    if(repoName.equals(parts[0])) {
        	    	SOURCE_FOLDER = parts[1];
        	    }
        	}
        	
        	if(SOURCE_FOLDER.equals(null)) {
        		VCSLogger.infoLogToCmd("Repository " + repoName + " does not exist.");
        	}
		} 
    	catch (Exception e) {
    		VCSLogger.infoLogToCmd("Exception in reading File: " + e);
		}
    	
    	//String SOURCE_FOLDER = "/home/shubham/VCSTemp/VCSDebug/VCSD_1_2/.vcs";
    	VCSLogger.infoLogToCmd("SOURCE Folder Absolute Path: " + SOURCE_FOLDER);
    	
    	//Compress the content on absolute path obtained from above
    	if(!SOURCE_FOLDER.equals(null)) {
    		
    		File directoryToZip = new File(SOURCE_FOLDER);
    		List<File> fileList = new ArrayList<File>();

    		ZipDirectory.getAllFiles(directoryToZip, fileList);
    		ZipDirectory.writeZipFile(directoryToZip, fileList); //Writes to the working directory
    		VCSLogger.infoLogToCmd("---Done");
    	}
    	else {
    		VCSLogger.infoLogToCmd("The repositiory is Empty or not instantiated on this local machine.");
    	}
    	String userWorkDir = System.getProperty("user.dir");
    	String party[] = repoName.split("\\.");
    	String fname = userWorkDir + "/" + party[0] + ".zip";
    	VCSLogger.infoLogToCmd("OUTPUT_ZIP_FILE: " + fname);
    	File f = new File(fname);
    	// Return Compressed File
		return f;
	}
    
    public File PullRepository(String repoName) {
    	
    	String userHomeDir = System.getProperty("user.home");
    	String userWorkDir = System.getProperty("user.dir");
    	
    	
    	String SOURCE_FOLDER = null;
    	try {

    		String fileName = userHomeDir + "/repoListHolder.txt";
    		List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
    		
        	for (String line : lines) {
        		String parts[] = line.split(" ");
        	    if(repoName.equals(parts[0])) {
        	    	SOURCE_FOLDER = parts[1] + Constants.VCSFOLDER;
        	    }
        	}
        	
        	if(SOURCE_FOLDER.equals(null)) {
        		VCSLogger.infoLogToCmd("Repository " + repoName + " does not exist.");
        	}
		} 
    	catch (Exception e) {
    		VCSLogger.infoLogToCmd("Exception in reading File: " + e);
		}
    	
    	VCSLogger.infoLogToCmd("SOURCE .VCS Folder's Absolute Path: " + SOURCE_FOLDER);

    	//Compress the content on absolute path obtained from above
    	if(!SOURCE_FOLDER.equals(null)) {
    		
    		File directoryToZip = new File(SOURCE_FOLDER);
    		List<File> fileList = new ArrayList<File>();

    		ZipDirectory.getAllFiles(directoryToZip, fileList);
    		ZipDirectory.writeZipFile(directoryToZip, fileList); //Writes to the working directory
    		VCSLogger.infoLogToCmd("---Done");
    	}
    	else {
    		VCSLogger.infoLogToCmd("The repositiory is Empty or not instantiated on this local machine.");
    	}
    	
    	String party[] = repoName.split("\\.");
    	String fname = userWorkDir + "/" + Constants.VCSFOLDER + ".zip";
    	VCSLogger.infoLogToCmd("OUTPUT_ZIP_FILE: " + fname);
    	
    	File f = new File(fname);
    	// Return Compressed File
		return f;
    }
    
}


