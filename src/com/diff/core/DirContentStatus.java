package com.diff.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DirContentStatus {
	
	public static enum Status{NEW,MODIFIED,EQUAL};
	private File file;
	private File otherFile;
	private Status status;
	private List<DirContentStatus> children;
	
	public DirContentStatus(File file, Status status){
		this(file);
		this.status = status;
	}
	
	public String toString(){
		return file.getAbsolutePath();
	}
	
	public DirContentStatus(File file){
		this.file = file;
		this.status = Status.EQUAL;
		children = new ArrayList<DirContentStatus>();
	}

	public File getFile(){
		return file;
	}
	
	public List<DirContentStatus> getChildren(){
		return children;
	}
	
	// this method only makes sense for modified files, so we
	// do not have to find in the other folder tree the corresponding
	// file to compute a file diff with both
	public File getOtherFile(){
		return otherFile;
	}
	
	public Status getStatus(){
		return status;
	}	
	
	public void setStatus(Status status){
		this.status = status;
	}
	
	public DirContentStatus addFileEqual(File file){
		DirContentStatus content = new DirContentStatus(file, Status.EQUAL);
		children.add(content);	
		return content;
	}
	
	public DirContentStatus addFileModified(File file, File otherFile){
		DirContentStatus content = new DirContentStatus(file, Status.MODIFIED);
		content.otherFile = otherFile;
		children.add(content);
		return content;
	}
	
	public DirContentStatus addFileNew(File file){
		DirContentStatus content = new DirContentStatus(file, Status.NEW);
		children.add(content);
		return content;
	}
	
	public void addAll(DirContentStatus externalContentStatus){
		children.addAll(externalContentStatus.children);		
	}
	
	@SuppressWarnings("unchecked")
	public void sort(){
		FileComparator comparator = new FileComparator();
		Collections.sort(children, comparator);
	}
	
	//////////////////////////////////
	
	@SuppressWarnings("rawtypes")
	private class FileComparator implements Comparator{

		public int compare(Object arg0, Object arg1) {
			int result = 0;
			DirContentStatus status1 = (DirContentStatus)arg0;
			DirContentStatus status2 = (DirContentStatus)arg1;
			if (status1.file.isDirectory()!=status2.file.isDirectory()){
				if (status1.file.isDirectory()){
					result = -1;
				}
				else{
					result = 1;
				}
			}			
			else{
				result = status1.file.getName().compareTo(status2.file.getName());
			}
			return result;
		}
		
	}

}
