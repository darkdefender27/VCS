package objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import vcs.Operations;

/**
 * Class representing <code>file</code> entity on disk.
 * Note: File content is not stored in java object.All operations related to content are done using java.io. 
 * @author warrior
 *
 */
public class VCSBlob extends AbstractVCSTree{
	
	/**
	 * Creates instance from <code>hashed file</code> which is stored on disk.
	 * @param objectHash @see {@link VCSObject#objectHash}
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 * @param diskPath @see {@link AbstractVCSTree#diskPath}
	 * @param name @see {@link AbstractVCSTree#name}
	 */
	public VCSBlob(String objectHash,String workingDirectory,String diskPath,String name){
		super(objectHash,workingDirectory,diskPath,name);
		this.type = "blob";
	}
	
	/**
	 * Creates instance form <code>un hashed file</code> which is stored on disk.
	 * It generates <code>SHA256</code> hash from the <code>file content<code> in the process.
	 * @param name {@link AbstractVCSTree#name}
	 * @param filePath @see {@link AbstractVCSTree#diskPath}
	 * @param workingDirectory @see {@link VCSObject#workingDirectory}
	 */
	public VCSBlob(String name,String filePath,String workingDirectory){
		super(name,filePath,workingDirectory);
		this.type = "blob";
		try {
			hashContent(filePath);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Hashes and writes file content to disk on location ./vcs/objects/objecthash.
	 * @return boolean Write status.
	 */
	@Override
	public boolean writeObjectToDisk(){
		//read file from filepath and compress it and write
		if(name!=null && diskPath !=null){
	        try {
	        	String objectFile = Operations.getObjectsFolder(workingDirectory) 
	        								+ "/" 
	        								+ objectHash.charAt(0) 
	        								+ objectHash.charAt(1) 
	        								+ "/" 
	        								+ objectHash.substring(2, objectHash.length());
	        	
	        	File objectDir = new File(Operations.getObjectsFolder(workingDirectory) 
	        								+ "/" + objectHash.charAt(0) 
	        								+ objectHash.charAt(1));
	        	if(!objectDir.exists()){
	        		boolean dirCreated = objectDir.mkdir();
	        		if(!dirCreated) return false;
	        	}
	        	File newFileCreated = new File(objectFile);
	        	if(newFileCreated.exists()){
				//dont write as file already in updated state
	        		return true;
	        	}
	        	boolean fileCreated = newFileCreated.createNewFile();
	        	if(!fileCreated) return false;
	        	
	        	FileInputStream fin=new FileInputStream(diskPath);  
	        	  
	        	FileOutputStream fout=new FileOutputStream(objectFile);  
	        	DeflaterOutputStream out=new DeflaterOutputStream(fout);  
	        	  
	        	int i;  
	        	while((i=fin.read())!=-1){  
	        	out.write((byte)i);  
	        	out.flush();  
	        	}  
	        	  
	        	fin.close();  
	        	out.close();  
	        	return true;
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } 
		}
		return false;
	}
	
	/**
	 * Reads, decompresses hashed file content and writes it to location specified by the {@link AbstractVCSTree#diskPath}.
	 * @return boolean Write Status.
	 */
	@Override
	public boolean writeOriginalToDisk(){
		try{  
			String objectFile = Operations.getObjectsFolder(workingDirectory) 
										+ "/" + objectHash.charAt(0) 
										+ objectHash.charAt(1) 
										+ "/" 
										+ objectHash.substring(2, objectHash.length());
			File diskObjectFile = new File(objectFile);
        	if(diskObjectFile.exists()){
        		FileInputStream fin=new FileInputStream(objectFile);  
    			InflaterInputStream in=new InflaterInputStream(fin);
    			
    			File unCompressedFile = new File(diskPath);
    			unCompressedFile.createNewFile();
    			FileOutputStream fout=new FileOutputStream(diskPath);
    			
    			int i;  
    			while((i=in.read())!=-1){  
    				fout.write((byte)i);  
    				fout.flush(); 
    			}
    			fin.close();  
    			fout.close();  
    			in.close();
    			return true;
        	}  
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Generates SHA256 hash of file content.
	 * @param absoluteFilePath
	 * @return String Hashed String or null otherwise.
	 */
	@Override
	public String hashContent(String absoluteFilePath) throws IOException, NoSuchAlgorithmException{
		if(absoluteFilePath !=null && !absoluteFilePath.isEmpty()){
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			FileInputStream fis = new FileInputStream(absoluteFilePath);
			byte[] dataBytes = new byte[1024];
			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) 
			{
				md.update(dataBytes, 0, nread);
			}
			fis.close();
			byte[] mdbytes = md.digest();
	
			//convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) 
			{
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			// convert the byte to hex format method 2
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) 
			{
				hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
			}
			this.objectHash = hexString.toString();
			return this.objectHash;
		}
		return null;
	}
	
	/**
	 * Get file content of the Blob.
	 * Reads whole file in memory so deprecated.
	 * @return String Content of file.
	 * @deprecated
	 */
	@Override
	public String getContent() {
		//return file content
		if(name !=null && diskPath!=null){
			
		}
		return null;
	}
	
	/**
	 * Returns file name as string so that it can be printed in tree format.
	 * @param indent number of tabs by which to indent.
	 */
	@Override
	public String printTree(int tab) {
		// TODO Auto-generated method stub''
		StringBuilder tabs = new StringBuilder();
		for(int i=0;i<tab;i++){
			tabs.append("\t");
		}
		return tabs.toString() + "File = " + name + "\n"; 
	}
	
	/**
	 * Deprecated to avoid getting file in main memory.
	 * @return String null always.
	 * @deprecated
	 */
	@Override
	protected String decompressObject() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public  boolean writeTreeToDisk(){
		return writeObjectToDisk();
	}
	
	
}
