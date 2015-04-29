package objects;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import logger.VCSLogger;

/**
 * Class representing Disk Folder entity.
 * @author warrior
 *
 */
public class VCSTree extends AbstractVCSTree{
	
	/**
	 * Holds immediate tress head elements.
	 */
	private ArrayList<AbstractVCSTree> immediateChildren;
	
	/**
	 * Create instance from hashed entity.
	 * @param objectHash @see {@link VCSObject#objectHash}
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 * @param diskPath @see {@link AbstractVCSTree#diskPath}
	 * @param name @see {@link AbstractVCSTree#name}
	 */
	public VCSTree(String objectHash,String workingDirectory,String diskPath,String name){
		super(objectHash,workingDirectory,diskPath,name);
		immediateChildren = new ArrayList<AbstractVCSTree>();
		this.type = "tree";
		generatingFromHash = true;
		createInMemory();
		VCSLogger.debugLogToCmd("VCSTree#", diskPath +" tree restored");
	}
	
	public VCSTree(String objectHash,String workingDirectory,String diskPath,String name,String tmpDirName){
		super(objectHash,workingDirectory,diskPath,name);
		this.readFromTempDir = true;
		this.readOpSourceTempDirName = tmpDirName;
		immediateChildren = new ArrayList<AbstractVCSTree>();
		this.type = "tree";
		generatingFromHash = true;
		createInMemory();
		VCSLogger.debugLogToCmd("VCSTree#", diskPath +" tree restored");
	}
	
	private boolean generatingFromHash;
	/**
	 * Creates instance from unhashed folder entity.
	 * Note:SHA256 hash calculation is responsibility class client.
	 * @param name @see {@link AbstractVCSTree#name}
	 * @param path @see {@link AbstractVCSTree#diskPath}
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 */
	public VCSTree(String name,String path,String workingDirectory){
		super(name, path,workingDirectory);
		immediateChildren = new ArrayList<AbstractVCSTree>();
		this.type = "tree";
		VCSLogger.debugLogToCmd("VCSTree#", diskPath + " tree initialised");
	}
	
	/**
	 * Generate tree content SHA256 hash. Note:SHA256 hash calculation is responsibility class client.
	 * @return String 40byte hash.
	 */
	public String generateTreeHash()
	{
		try 
		{
			return hashContent(getContent());
		}
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
			//VCSLogger.errorLogToCmd("VCSTree#generateTreeHash", e.toString());
	    }
		catch (IOException e) 
		{
			e.printStackTrace();
			//VCSLogger.errorLogToCmd("VCSTree#generateTreeHash", e.toString());
		}
		return null;
	}
	
	/**
	 * Adds AbstractVCSTree element.
	 * @param element {@link AbstractVCSTree} element to add.
	 * @return boolean Add status.
	 */
	public boolean addItem(AbstractVCSTree element)
	{
		if(immediateChildren!=null)
		{
			immediateChildren.add(element);
			if(!generatingFromHash){
				generateTreeHash();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Return tree description. Each line contains description of immediate child.
	 * @return String Tree description.
	 */
	@Override
	public String getContent() 
	{
		/*
		 * hash~type~name
		 * hash~type~name
		 * ..
		 * .
		 */
		if(immediateChildren !=null)
		{
			StringBuilder contentBuilder = new StringBuilder();
			
			for(int i = 0;i< immediateChildren.size();i++)
			{
				AbstractVCSTree element = immediateChildren.get(i);
				contentBuilder.append(element);
				contentBuilder.append("\n");
			}

			//NOTE last \n remaining
			//System.out.print(contentBuilder.toString());
			return contentBuilder.toString();
		}
		return null;
	}
	
	/**
	 * Writes complete tree to disk starting from current folder up to leaves.
	 * @return boolean Write status.
	 */
	@Override
	public boolean writeOriginalToDisk() 
	{
		// TODO Auto-generated method stub
		//createInMemory();
		//System.out.println(immediateChildren.size());
		File selfFolder = new File(diskPath);
		selfFolder.mkdir();
		Iterator<AbstractVCSTree> it = immediateChildren.listIterator();
		while(it.hasNext())
		{
			(it.next()).writeOriginalToDisk();
		}
		VCSLogger.debugLogToCmd("VCSTree#writeOriginalToDisk", diskPath + " tree restored");
		return true;
	}
	
	/**
	 * Checks AbstractVCSTree object exist in immediate children list.
	 * @param name Name of immediate child.
	 * @param type Type of immediate child.
	 * @return boolean Exist Status.
	 */
	public AbstractVCSTree getIfExist(String name,String type)
	{
		Iterator<AbstractVCSTree> it = immediateChildren.iterator();
		while(it.hasNext())
		{
			AbstractVCSTree item = it.next();
			if(item.name.equals(name) && item.getType().equals(type))
			{
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Returns string in tree format.
	 * @param tab number of tabs to print before first line.
	 * @return String String containing folder name.
	 */
	@Override
	public String printTree(int tab){
		StringBuilder builder = new StringBuilder();
		Iterator<AbstractVCSTree> it = immediateChildren.iterator();
		StringBuilder tabs = new StringBuilder();
		for(int i=0;i<tab;i++){
			tabs.append("\t");
		}
		builder.append(tabs.toString()+"Folder = " + name +"\n");
		//System.out.println(immediateChildren.size());
		while(it.hasNext()){
			AbstractVCSTree item = it.next();
			builder.append(item.printTree(tab+1));
		}
		return builder.toString();
	}
	
	private boolean createInMemory(){
		//System.out.println("create in memory");
		String content = decompressObject();
		//System.out.println(content);
		this.immediateChildren.clear();
		String[] treeItemsInString = content.split("\n");
		String[] objectFeatures = null;
		String objectPath=null;
		String objectHash = null;
		String objectName = null;
		String objectType =null;
		AbstractVCSTree object =null;
		for(int i=0;i<treeItemsInString.length;i++){
			objectFeatures= treeItemsInString[i].split(SEPARATOR);
			objectHash = objectFeatures[0];
			objectType = objectFeatures[1];
			objectName = objectFeatures[2];
			//System.out.println(objectHash +" " + objectType + " " + objectName);
			//System.out.println("Name = " + objectName);
			if(objectType.equals("tree")){
				objectPath = diskPath +objectName + "/";
				if(!this.readFromTempDir){
					object = new VCSTree(objectHash, workingDirectory,objectPath,objectName);
				}else{
					object = new VCSTree(objectHash, workingDirectory,objectPath,objectName,this.readOpSourceTempDirName);
				}
			}
			else if(objectType.equals("blob")){
				objectPath = diskPath + objectName;
				if(!readFromTempDir){
					object = new VCSBlob(objectHash, workingDirectory,objectPath,objectName);
				}else{
					object = new VCSBlob(objectHash, workingDirectory,objectPath,objectName,readOpSourceTempDirName);
				}
			}
			addItem(object);
		}
		return true;
	}

//	/**
//	 * Returns complete tree which is kept in memory.
//	 * @return {@link AbstractVCSTree} inMemoryTree
//	 */
//	public VCSTree createInMemoryTree(){
//		//createInMemory();
//		return this;
//	}
	
	/**
	 * Write complete hashed tree to disk
	 * @return boolean write status
	 */
	public  boolean writeTreeToDisk(){
		boolean treeWriteStatus =false;
		if(isModified()){
			treeWriteStatus = this.writeObjectToDisk();
			VCSLogger.debugLogToCmd("VCSTree#writeTreeDisk", diskPath +" tree written to disk");
			Iterator<AbstractVCSTree> it = immediateChildren.listIterator();
			if(treeWriteStatus){
				while(it.hasNext()){
					(it.next()).writeTreeToDisk();
				}
			}
		}
		return treeWriteStatus;
	}
	
	public AbstractVCSTree getAtIndex(int i){
		return immediateChildren.get(i);
	}
	
	public ArrayList<AbstractVCSTree> getImmediateChildren(){
		return immediateChildren;
	}

	public AbstractVCSTree findTreeIfExist(String relativePath, int index){
		//System.out.println("reached "+relativePath);
		String path[] = relativePath.split("/");
		boolean lastElement = path.length - 1 == index ? true:false;
		String currentEleType = lastElement? "blob" :"tree";
		AbstractVCSTree ele = getIfExist(path[index],currentEleType);
		if(ele != null)
		{
			if(ele.getType() == "tree")
			{
				ele = ((VCSTree)ele).findTreeIfExist(relativePath, index+1);
				//System.out.println("in if"+ele.name);
				return ele;
			}
			else
			{
				//System.out.println("in else"+ele.name);
				return ele;
			}
		}
		//System.out.println("before return"+ele.name);
		return null;
	}
}
