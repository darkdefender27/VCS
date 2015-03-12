package com.diff.core;

import java.io.File;

public class DirDiffResult {

	private DirContentStatus leftDirStatus;
	private DirContentStatus rightDirStatus;
	
	public DirDiffResult(File leftFolder, File rightFolder){
		leftDirStatus = new DirContentStatus(leftFolder);
		rightDirStatus = new DirContentStatus(rightFolder);
	}
	
	public DirContentStatus getLeftContent(){
		return leftDirStatus;
	}
	
	public DirContentStatus getRightContent(){
		return rightDirStatus;
	}
}
