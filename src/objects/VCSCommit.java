package objects;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import logger.VCSLogger;

/**
 * Class representing Commit object.
 * @author warrior
 *
 */
public class VCSCommit extends VCSObject
{
	/**
	 * number of lines inserted in all of the staged files 
	 */
	private int noOfLinesInserted;
	
	/**
	 * number of lines deleted in all of the staged files
	 */
	private int noOfLinesDeleted;
	
	public int getNoOfLinesInserted() {
		return noOfLinesInserted;
	}

	public void setNoOfLinesInserted(int noOfLinesInserted) {
		this.noOfLinesInserted = noOfLinesInserted;
	}

	public int getNoOfLinesDeleted() {
		return noOfLinesDeleted;
	}

	public void setNoOfLinesDeleted(int noOfLinesDeleted) {
		this.noOfLinesDeleted = noOfLinesDeleted;
	}
	
	/**
	 * the time of the commit
	 */
	private long commitTimestamp;
	
	/**
	 * the branch to which the commit belongs 
	 */
	private String branchName;
	
	/**
	 * Parent commit. Null if no parent exist.
	 */
	//private VCSCommit parentCommit;
	
	/**
	 * Holds committed tree pointer.
	 */
	private VCSTree tree;
	/**
	 * Holds commit message
	 */
	private String commitMessage;
	/**
	 * Author of repository
	 */
	private String author;
	/**
	 * Committer of commit
	 */
	private String committer;
	
	/**
	 * Pass in constructor {@link VCSCommit#VCSCommit(String, String, int)}
	 * Tells constructor to import commit and it's corresponding tree, here {@link VCSCommit#parentCommit} will be null
	 */
	public static final int IMPORT_TREE = 0;
	/**
	 * Pass in constructor {@link VCSCommit#VCSCommit(String, String, int)}
	 * Tells constructor to import commits list, here {@link VCSCommit#tree} will be null
	 */
	public static final int IMPORT_COMMITS = 1;
	/**
	 * Pass in constructor {@link VCSCommit#VCSCommit(String, String, int)}
	 * Tells constructor to import only commit, here both {@link VCSCommit#tree} and {@link VCSCommit#parentCommit} will be null
	 */
	public static final int IMPORT_JUST_COMMIT = 2;
	
	private int importFlag = IMPORT_TREE;
	
	private ArrayList<VCSCommit> parents = new ArrayList<>();
	/**
	 * Create instance and generates SHA256 hash in process.
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 * @param parentCommit @see {@link #parentCommit}
	 * @param tree @see {@link #tree}
	 * @param commitMessage @see {@link #commitMessage}
	 * @param author @see {@link #author}
	 * @param committer @see {@link #committer}
	 */
	public VCSCommit(String workingDirectory,VCSCommit parentCommit, VCSTree tree,
			String commitMessage, String author, String committer) 
	{
		super(workingDirectory);
		if(parentCommit != null) parents.add(parentCommit);
		this.tree = tree;
		this.commitMessage = commitMessage;
		this.author = author;
		this.committer = committer;
		this.type = "commit";
		try 
		{
			hashContent(getContent());
		}
		catch (NoSuchAlgorithmException | IOException e) 
		{
			//TODO Auto-generated catch block
			VCSLogger.errorLogToCmd("VCSCommit#", e.toString());
	    }
		VCSLogger.debugLogToCmd("VCSCommit#", "commit initialised");
	}
	
	public VCSCommit(String objectHash,String workingDirectory,int importFlag){
		super(workingDirectory);
		this.objectHash = objectHash;
		this.importFlag = importFlag;
		createInMemory();
		//VCSLogger.debugLogToCmd("VCSCommit#", objectHash + " commit restored");
	}

	/**
	 * Returns commit object hash.
	 */
	public String toString()
	{
		return this.objectHash;
	}
	
	/**
	 * Returns commit content.
	 * @return String Content in format
	 * "tree
	 * 	parent hash
	 * 	author name
	 * 	committer name
	 * 	commitMessage"
	 */
	@Override
	public String getContent() 
	{
		/*
		 * tree~hash~name~relativePath
		 * Parents~#parents
		 * parent~(commit)hash
		 * ....
		 * ....
		 * author~name
		 * committer~name
		 * message
		 */
		if(tree !=null && commitMessage!=null && author!=null){

			StringBuilder contentBuilder = new StringBuilder();
			contentBuilder.append("tree"+SEPARATOR);
			contentBuilder.append(tree.getObjectHash());
			contentBuilder.append(SEPARATOR);
			contentBuilder.append(tree.getName());
			contentBuilder.append(SEPARATOR);
			contentBuilder.append(tree.getRelativePath());
			
			contentBuilder.append("\nParents"+SEPARATOR);
			contentBuilder.append(String.valueOf(parents.size()));
			for(VCSCommit parentCommit: parents){
				contentBuilder.append("\nparent"+SEPARATOR);
				if(parentCommit!=null)
				{
					contentBuilder.append(parentCommit.getObjectHash());
				}
			}
			contentBuilder.append("\nauthor"+SEPARATOR);
			contentBuilder.append(this.author);
			
			contentBuilder.append("\ncommitter"+SEPARATOR);
			contentBuilder.append(this.committer);
			
			contentBuilder.append("\n");
			contentBuilder.append(this.commitMessage);
			
			contentBuilder.append("\n");
			contentBuilder.append(this.noOfLinesInserted);
			
			contentBuilder.append("\n");
			contentBuilder.append(this.noOfLinesDeleted);
			
			contentBuilder.append("\n");
			contentBuilder.append(this.commitTimestamp);
			
			contentBuilder.append("\n");
			contentBuilder.append(this.branchName);
			return contentBuilder.toString();
		}
		return null;
	}
	private boolean createInMemory(){
		//System.out.println("create in memory");
		String content = decompressObject();
		//System.out.println(content);
		
		if(content != null){
			String[] commitItemsInString = content.split("\n");
		
			String[] treeFeatures = commitItemsInString[0].split(SEPARATOR);
			
			String tree = treeFeatures[1];
			String treeName = treeFeatures[2];
			String treeRelativePath = treeFeatures[3];
			
			String parentsNumberFeatures[] = commitItemsInString[1].split(SEPARATOR);
			
			int numberOfParents = Integer.parseInt(parentsNumberFeatures[1]);
			for(int i = 0;i<numberOfParents ;i++){
				String[] commitFeatures = commitItemsInString[i+2].split(SEPARATOR);
				String parentCommit = null;
				if(commitFeatures.length != 1){
					parentCommit = commitFeatures[1];
					if(importFlag == IMPORT_COMMITS){
						this.parents.add(
								new VCSCommit(parentCommit, workingDirectory,IMPORT_COMMITS));
					}
				}
			}
			
			this.author = commitItemsInString[numberOfParents + 2].split(SEPARATOR)[1];
			this.committer = commitItemsInString[numberOfParents + 3].split(SEPARATOR)[1];
			this.commitMessage = commitItemsInString[numberOfParents + 4].split(SEPARATOR)[0];

			this.noOfLinesInserted=Integer.parseInt(commitItemsInString[numberOfParents + 5].split(SEPARATOR)[0]);
			this.noOfLinesDeleted=Integer.parseInt(commitItemsInString[numberOfParents + 6].split(SEPARATOR)[0]);
			this.commitTimestamp=Long.parseLong(commitItemsInString[numberOfParents + 7].split(SEPARATOR)[0]);
			this.branchName=commitItemsInString[numberOfParents+8].split(SEPARATOR)[0];
			if(importFlag == IMPORT_TREE){
				this.tree =new VCSTree(tree, workingDirectory,workingDirectory + treeRelativePath,treeName);
			}
			return true;
		}
		return false;
	}
	
	public boolean writeCommitToDisk(){
		boolean status = writeObjectToDisk();
		if(status) 
		{
			status = tree.writeTreeToDisk();
		}
		VCSLogger.debugLogToCmd("VCSCommit#writeCommitToDisk", objectHash +" commit writtent to disk");
		return status;
	}
	
	public ArrayList<VCSCommit> getParentCommits() {
		return parents;
	}

	public VCSTree getTree() {
		return tree;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public String getAuthor() {
		return author;
	}

	public String getCommitter() {
		return committer;
	}

	public long getCommitTimestamp() {
		return commitTimestamp;
	}

	public void setCommitTimestamp(long commitTimestamp) {
		this.commitTimestamp = commitTimestamp;
	}
	
	public void addParent(VCSCommit parent){
		parents.add(parent);
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
}
