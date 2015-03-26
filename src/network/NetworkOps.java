package network;

import java.io.File;
import network.HashUtil;
import network.AppZip;
import logger.VCSLogger;

public class NetworkOps {

    public File CloneRepository(String repoName) {
    	//String absPath = get repo rootdir absolute path
    	//compress .vcs to vcs.zip
    	//File f = new File(absPath)
	
    	//Zip File Name to be transferred over the network.
    	String OUTPUT_ZIP_FILE = "/home/shubham/VCSTemp/VCSRepo.zip";
    	
    	//MAP the uri's to Absolute Path (repoName [abcd.vcs] -> absolutePath of root directory)
    	
    	//String SOURCE_FOLDER = HashUtil.getValue(repoName); // e.g.: VCSD_1_0.vcs
    	String SOURCE_FOLDER = "/home/shubham/VCSTemp/VCSDebug/VCSD_1_0/.vcs/";
    	VCSLogger.infoLogToCmd("SOURCE Folder: " + SOURCE_FOLDER);
    	
    	
    	//Compress the content on absolute path obtained from above
    	
    	AppZip appZip = new AppZip();
    	appZip.generateFileList(new File(SOURCE_FOLDER));
    	appZip.zipIt(OUTPUT_ZIP_FILE);
    	
    	File f = new File(OUTPUT_ZIP_FILE);
    	
    	// Return Compressed File
		return f;
	}
}
