package objects;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

/**
 * Class representing Commit object.
 * @author warrior
 *
 */
public class VCSCommit extends VCSObject{
	
	/**
	 * Parent commit. Null if no parent exist.
	 */
	private VCSCommit parentCommit;
	
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
	 * Create instance and generates SHA256 hash in process.
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 * @param parentCommit @see {@link #parentCommit}
	 * @param tree @see {@link #tree}
	 * @param commitMessage @see {@link #commitMessage}
	 * @param author @see {@link #author}
	 * @param committer @see {@link #committer}
	 */
	public VCSCommit(String workingDirectory,VCSCommit parentCommit, VCSTree tree,
			String commitMessage, String author, String committer) {
		super(workingDirectory);
		this.parentCommit = parentCommit;
		this.tree = tree;
		this.commitMessage = commitMessage;
		this.author = author;
		this.committer = committer;
		this.type = "commit";
		try {
			hashContent(getContent());
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public VCSCommit(String objectHash,String workingDirectory,boolean importTree){
		super(workingDirectory);
		this.objectHash = objectHash;
		createInMemory();
	}

	/**
	 * Returns commit object hash.
	 */
	public String toString(){
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
	public String getContent() {
		/*
		 * tree hash name path
		 * parent(commit) hash
		 * author name
		 * committer name
		 * message
		 */
		if(tree !=null && commitMessage!=null && author!=null){

			StringBuilder contentBuilder = new StringBuilder();
			contentBuilder.append("tree ");
			contentBuilder.append(tree.getObjectHash());
			contentBuilder.append(" ");
			contentBuilder.append(tree.getName());
			contentBuilder.append(" ");
			contentBuilder.append(tree.getPath());
			
			contentBuilder.append("\nparent ");
			if(parentCommit!=null) contentBuilder.append(parentCommit.getObjectHash());
			
			contentBuilder.append("\nauthor ");
			contentBuilder.append(this.author);
			
			contentBuilder.append("\ncommitter ");
			contentBuilder.append(this.committer);
			
			contentBuilder.append("\n");
			contentBuilder.append(this.commitMessage);
			
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
		
			String[] treeFeatures = commitItemsInString[0].split(" ");
			
			String tree = treeFeatures[1];
			String treeName = treeFeatures[2];
			String treePath = treeFeatures[3];
			
			String[] commitFeatures = commitItemsInString[1].split(" ");
			String parentCommit = null;
			if(commitFeatures.length != 1){
				parentCommit = commitFeatures[1];
			}
			this.author = commitItemsInString[2].split(" ")[1];
			this.committer = commitItemsInString[3].split(" ")[1];
			this.commitMessage = commitItemsInString[4].split(" ")[0];
			
			this.tree =new VCSTree(tree, workingDirectory,treePath,treeName);
			//if(parentCommit != null)
				//this.parentCommit = new VCSCommit(parentCommit, workingDirectory);
			
			return true;
		}
		return false;
	}
	
	public boolean writeCommitToDisk(){
		boolean status = writeObjectToDisk();
		if(status) status = tree.writeTreeToDisk();
		return status;
	}

	public VCSCommit getParentCommit() {
		return parentCommit;
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
	
}
