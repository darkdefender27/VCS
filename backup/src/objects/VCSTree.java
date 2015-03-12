package objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.InflaterInputStream;

import vcs.Operations;
import vcs.VCS;

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
		createInMemory();
	}
	
	/**
	 * Creates instance from unhashed folder entity.
	 * Note:SHA256 hash calculation is repsonsibility class client.
	 * @param name @see {@link AbstractVCSTree#name}
	 * @param path @see {@link AbstractVCSTree#diskPath}
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 */
	public VCSTree(String name,String path,String workingDirectory){
		super(name, path,workingDirectory);
		immediateChildren = new ArrayList<AbstractVCSTree>();
		this.type = "tree";
	}
	
	/**
	 * Generate tree content SHA256 hash. Note:SHA256 hash calculation is repsonsibility class client.
	 * @return String 40byte hash.
	 */
	public String generateTreeHash(){
		try {
			return hashContent(getContent());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Adds AbstractVCSTree element.
	 * @param element {@link AbstractVCSTree} element to add.
	 * @return boolean Add status.
	 */
	public boolean addItem(AbstractVCSTree element){
		if(immediateChildren!=null){
			immediateChildren.add(element);
			generateTreeHash();
			return true;
		}
		return false;
	}
	/**
	 * Return tree description. Each line contains description of immediate child.
	 * @return String Tree description.
	 */
	@Override
	public String getContent() {
		/*
		 * hash type name
		 * hash type name
		 * ..
		 * .
		 */
		if(immediateChildren !=null){
			StringBuilder contentBuilder = new StringBuilder();
			
			for(int i = 0;i< immediateChildren.size();i++){
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
	public boolean writeOriginalToDisk() {
		// TODO Auto-generated method stub
		//createInMemory();
		//System.out.println(immediateChildren.size());
		File selfFolder = new File(diskPath);
		if(!selfFolder.mkdir()) return false;
		Iterator<AbstractVCSTree> it = immediateChildren.listIterator();
		while(it.hasNext()){
			(it.next()).writeOriginalToDisk();
		}
		return true;
	}
	
	/**
	 * Checks AbstractVCSTree object exist in immediate children list.
	 * @param name Name of immediate child.
	 * @param type Type of immediate child.
	 * @return boolean Exist Status.
	 */
	public AbstractVCSTree getIfExist(String name,String type){
		Iterator<AbstractVCSTree> it = immediateChildren.iterator();
		while(it.hasNext()){
			AbstractVCSTree item = it.next();
			if(item.name.equals(name) && item.getType().equals(type))
				return item;
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
			objectFeatures= treeItemsInString[i].split(" ");
			objectHash = objectFeatures[0];
			objectType = objectFeatures[1];
			objectName = objectFeatures[2];
			//System.out.println(objectHash +" " + objectType + " " + objectName);
			//System.out.println("Name = " + objectName);
			if(objectType.equals("tree")){
				objectPath = diskPath + "/"+objectName + "/";
				object = new VCSTree(objectHash, workingDirectory,objectPath,objectName);
			}
			else if(objectType.equals("blob")){
				objectPath = diskPath +"/"+ objectName;
				object = new VCSBlob(objectHash, workingDirectory,objectPath,objectName);
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
		boolean status = this.writeObjectToDisk();
		Iterator<AbstractVCSTree> it = immediateChildren.listIterator();
		while(it.hasNext() && status){
			status = (it.next()).writeTreeToDisk();
		}
		return status;
	}
	
	public AbstractVCSTree getAtIndex(int i){
		return immediateChildren.get(i);
	}
	
	public ArrayList<AbstractVCSTree> getImmediateChildren(){
		return immediateChildren;
	}
}