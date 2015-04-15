package objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import logger.VCSLogger;

import vcs.Constants;
import vcs.Operations;

/**
 * <code>Abstract</code> class representing VCSObject.
 * @author warrior
 *
 */
public abstract class VCSObject {
	/**
	 * 40 characters long SHA256 object hash
	 */
	protected String objectHash;
	/**
	 * Absolute directory path of versioned directory
	 */
	protected String workingDirectory;
	/**
	 * Type of object.
	 * Type can be blob,tree or a commit.
	 * Note: {@link #objectHash} must be set while extending object.
	 */
	protected String type = "Object";
	public VCSObject(String workingDirectory){
		this.workingDirectory = workingDirectory;
	}
	
	/**
	 * Returns object type.
	 * @return String @see {@link #type}.
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * <code>Abstract</code> method to get the object content in string format.
	 * @return String Object content
	 */
	public abstract String getContent();
	
	/**
	 * Returns SHA256 hash of given String.
	 * @param Content String whose hash to generate
	 * @return String 40 byte hash
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public String hashContent(String Content) throws IOException, NoSuchAlgorithmException{
		if(Content !=null && !Content.isEmpty()){
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] contentByte = Content.getBytes();
			md.update(contentByte, 0, contentByte.length);
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
	 * Returns SHA256 hash of object.
	 * @return @see {@link #objectHash}
	 */
	public String getObjectHash(){
		return objectHash;
	}
	
	/**
	 * Hashes and writes content of object to path /.vcs/objects/objectHash
	 * @return boolean Write status.
	 */
	public  boolean writeObjectToDisk(){
		String Content = getContent();
		//compress and write content to disk
		if(Content!=null && objectHash!=null){
			
			try {
				//write to objects/hash[0]hash[1]/hash[2..39] 
				String objectFile = Operations.getObjectsFolder(workingDirectory) 
																+ "/" + objectHash.charAt(0) 
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
				//dont write,file already in updated state
	        		return true;
	        	}
	        	boolean fileCreated = newFileCreated.createNewFile();
	        	if(!fileCreated) return false;
	        	FileOutputStream fout=new FileOutputStream(objectFile);  
	        	DeflaterOutputStream out=new DeflaterOutputStream(fout);  
	        	byte[] contentBytes = Content.getBytes();
	        	out.write(contentBytes, 0, contentBytes.length);  
	        	out.close(); 
				return true;
			} catch (IOException e) {
				VCSLogger.errorLogToCmd("VCSObject#writeObjectToDisk", e.toString());
		    }
		}
		return false;
	}
	
	protected boolean readFromTempDir;
	protected String readOpSourceTempDirName;
	
	/**
	 * Reads and un hashes object.
	 * @return String Decompressed Object Content
	 */
	protected String decompressObject(){
		//System.out.println("Decompress object");
		try{
			String objectFile = null;
			if(!readFromTempDir){
				objectFile= Operations.getObjectsFolder(workingDirectory);
			}else{
				objectFile = workingDirectory + ".vcs/" + readOpSourceTempDirName + "/.vcs/" + Constants.OBJECTS_FOLDER;
			}
			
			objectFile += "/" + objectHash.charAt(0) + objectHash.charAt(1) + "/" + objectHash.substring(2, objectHash.length());
			//System.out.println(objectFile);
			File diskObjectFile = new File(objectFile);
        	if(diskObjectFile.exists()){
        		FileInputStream fin=new FileInputStream(objectFile);  
    			InflaterInputStream in=new InflaterInputStream(fin);
    			
    			StringBuilder builder = new StringBuilder();
    			int i;
    			byte[] contentBytes = new byte[1];
    			while((i=in.read())!=-1){
    				contentBytes[0] = (byte)i; 
    				builder.append(new String(contentBytes, "UTF-8"));
    				//System.out.print((byte)i);
    			}
    			fin.close();
    			in.close();
    			//System.out.println(builder.toString());
    			return builder.toString();
        	}  
		}catch(Exception e){
			VCSLogger.errorLogToCmd("VCSObject#decompressObject", e.toString());
	    }
		return null;
	}

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	
	protected final static String SEPARATOR = "~";
}
