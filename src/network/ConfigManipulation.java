package network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import logger.VCSLogger;

public class ConfigManipulation {

	private File config;
	
	public ConfigManipulation(String configPath) {
		// TODO Auto-generated constructor stub
		this.config = new File(configPath);		
	}
	/**
	 * Writes to config file in the local .vcs folder per repository.
	 * Cause: VCS Init, VCS Clone, VCS Remote Add
	 * Purpose: Keep track of remote URLs
	 */
	public void writeInitConfig(String configPath) {
		//Code to write to config file on Repo Init.
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.config, false)))) {
		    out.println("[core]\n\tbare=false"); 
		}
		catch (IOException e) {
			VCSLogger.infoLogToCmd("Data write InitConfig failed!");
		}
	}
	
	public void writeCloneConfig(String repoRemoteUrl) {
		//Code to write to config file on Clone.
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.config, true)))) {
		    out.println("[remote origin]\n\turl="+repoRemoteUrl); 
		}
		catch (IOException e) {
			VCSLogger.infoLogToCmd("Data write InitConfig failed!");
		}
	}
	
	public void writeRemoteAddConfig() {
		//Code to write to config file on remote addition. 
	}
}
