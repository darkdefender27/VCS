package objects;

import java.util.Iterator;

/**
 * <code>Abstract</code> class for holding object that represent some entity on disk.
 * Entity can be a <code>folder</code> or a <code>file</code>.
 * @author warrior
 *
 */
public abstract class AbstractVCSTree extends VCSObject
{
	/**
	 * Holds <code>absolute path</code> of the unhashed entity on disk.
	 */
	
	protected String diskPath;
	/**
	 * Holds <code>name</code> of the un hashed entity. 
	 */
	protected String name;
	
	protected String relativePath;

	/**
	 * Creates instance from <code>hashed entity</code> which was stored on disk.
	 * @param objectHash @see {@link VCSObject#objectHash}
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 * @param diskPath @see {@link #diskPath}
	 * @param name @see {@link #name}
	 */
	public AbstractVCSTree(String objectHash,String workingDirectory,String diskPath,String name){
		super(workingDirectory);
		this.objectHash = objectHash;
		this.diskPath = diskPath;
		this.name = name;
		this.relativePath = this.diskPath.substring(this.workingDirectory.length()-1, this.diskPath.length());
	}
	/**
	 * Creates instance from <code>entity</code> which was stored on disk.
	 * @param name @see {@link #name}
	 * @param diskPath @see {@link #diskPath}
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 */
	public AbstractVCSTree(String name,String diskPath,String workingDirectory){
		super(workingDirectory);
		this.name = name;
		this.diskPath = diskPath;
		this.relativePath = this.diskPath.substring(this.workingDirectory.length()-1, this.diskPath.length());
	}
	
	/**
	 * <code>Abstract</code> method which children classes must implement
	 * to read then write the hashed entity to disk at {@link #diskPath}.
	 * @return boolean Write status
	 */
	public abstract boolean writeOriginalToDisk();
	
	/**
	 * <code>Abstract</code> method which children class must implement such that it can be printed in tree format.
	 * @param tab number of tabs for first line indentation
	 * @return String String containing tree structure
	 */
	public abstract String printTree(int tab);
	
	/**
	 * Tells deflator which files to write while deflating, i.e which files to add in current commit
	 * set to true to add in next commit
	 */
	private boolean modified;
	/**
	 * Returns the content of object in string of form "objectHash type name".
	 * @return String Format "objectHash type name"
	 */
	public String toString(){
		//return format -> hash~type~name
		if(name!=null && diskPath!=null){
			StringBuilder contentbuilder = new StringBuilder();
			
			contentbuilder.append(this.getObjectHash());
			contentbuilder.append(SEPARATOR);
			
			contentbuilder.append(this.getType());
			contentbuilder.append(SEPARATOR);
			
			contentbuilder.append(this.name);
			return contentbuilder.toString();
		}
		return null;
	}
	
	public abstract boolean writeTreeToDisk();
	
	public String getName(){
		return name;
	}
	public String getPath(){
		return diskPath;
	}
	
	public void setModified(boolean modified){
		this.modified = modified;
	}
	
	public boolean isModified(){
		return modified;
	}
	public String getRelativePath(){
		return relativePath;
	}
}
