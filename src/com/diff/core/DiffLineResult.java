package com.diff.core;

public class DiffLineResult 
{
	private int noOfLinesAdded;
    private int noOfLinesDeleted;
    private int noOfLinesUnmodified;
    
	public int getNoOfLinesAdded() {
		return noOfLinesAdded;
	}

	public void setNoOfLinesAdded(int noOfLinesAdded) {
		this.noOfLinesAdded = noOfLinesAdded;
	}

	public int getNoOfLinesDeleted() {
		return noOfLinesDeleted;
	}

	public void setNoOfLinesDeleted(int noOfLinesDeleted) {
		this.noOfLinesDeleted = noOfLinesDeleted;
	}

	public int getNoOfLinesUnmodified() {
		return noOfLinesUnmodified;
	}

	public void setNoOfLinesUnmodified(int noOfLinesUnmodified) {
		this.noOfLinesUnmodified = noOfLinesUnmodified;
	}
}
