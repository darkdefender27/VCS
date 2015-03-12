package com.diff.core;

import java.util.ArrayList;

public class ParsedFile {

	private FileLine lines[];

	public ParsedFile(String fileContent) {
		//List<FileLine> lineArray = new ArrayList<FileLine>();
		//char chars[] = fileContent.toCharArray();
		//StringBuffer buf = new StringBuffer();
		/*for(int i=0;i<chars.length;i++)
		{
			if (chars[i]=='\r' && (i==(chars.length-1) || chars[i+1]!='\n'))
			{
				lineArray.add(new FileLine(buf.toString(),lineArray.size(),EolType.CR));
				buf = new StringBuffer();
			}
			if (chars[i]=='\n')
			{
				lineArray.add(new FileLine(buf.toString(),lineArray.size(),EolType.LF));
				buf = new StringBuffer();
			}
			else if (chars[i]=='\r' && i<chars.length-1 && chars[i+1]=='\n')
			{
				lineArray.add(new FileLine(buf.toString(),lineArray.size(),EolType.CRLF));
				buf = new StringBuffer();
				i++;
			}
			else
			{
				buf.append(chars[i]);
			}
		}
		String str = buf.toString();
		if (str.length()>0){
			lineArray.add(new FileLine(str,lineArray.size(),EolType.NO_EOL));
		}
		lines = lineArray.toArray(new FileLine[0]);
		System.out.println("parsedFile  "+lines.length);*/
		
		String[] fileLines = fileContent.split(System.lineSeparator());
		//System.out.println("ParsedFile constructor "+fileLines.length);
		boolean endsWithNewLine = (fileContent.endsWith("\r\n") || fileContent.endsWith("\n") || fileContent.endsWith("\r"));
		lines = new FileLine[fileLines.length+(endsWithNewLine?1:0)];
		for(int i=0;i<fileLines.length;i++)
		{
			lines[i] = new FileLine(fileLines[i],i);
		}
		if (endsWithNewLine){
			lines[lines.length-1] = new FileLine("",lines.length-1);
		}
		
	}

	public ParsedFile(ArrayList<FileLine> lineArray){
		lines = new FileLine[lineArray.size()];
		for(int i=0;i<lineArray.size();i++){
			lines[i] = (FileLine)lineArray.get(i);
		}
	}

	public ParsedFile(FileLine lines[]){
		this.lines = lines;
	}

	public FileLine[] getLines(){
		return lines;
	}
}
