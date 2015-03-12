package com.diff.core;

public class FileDiffResult {

    private ParsedFile leftFile, rightFile;
    private DiffLineResult lineResult;
    

    public FileDiffResult(ParsedFile leftFile, ParsedFile rightFile) 
    {
        this.leftFile = leftFile;
        this.rightFile = rightFile;
    }

    public ParsedFile getLeftFile()
    {
        return leftFile;
    }

    public ParsedFile getRightFile()
    {
        return rightFile;
    }

	public DiffLineResult getLineResult() {
		return lineResult;
	}

	public void setLineResult(DiffLineResult lineResult) {
		this.lineResult = lineResult;
	}
}
