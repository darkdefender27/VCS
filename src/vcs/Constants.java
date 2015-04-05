package vcs;

/**
 * Holds constants related to VCS System implementation.
 */
public class Constants {
	
	public static final int MAX_FILES_STAGED = 100;

	/**
	 * Name of the folder where VCS metadata will be stored on disk.
	 */
	public static String VCSFOLDER = ".vcs";
	
	/**
	 * <code>Name</code> of the folder where <code>branch</code> data will be stored on disk.
	 */
	public static String BRANCH_FOLDER="branches";
	
	/**
	 * <code>Name</code> of the folder where <code>hooks</code> data will be stored disk.
	 */
	public static String HOOKS_FOLDER="hooks";

	/**
	 * <code>Name</code> of the folder where <code>info</code> data will be stored disk.
	 */
	public static String INFO_FOLDER="info";

	/**
	 * <code>Name</code> of the folder where <code>logs</code> data will be stored disk.
	 */
	public static String LOGS_FOLDER="logs";

	/**
	 * <code>Name</code> of the folder where <code>objects</code> data will be stored disk.
	 */
	public static String OBJECTS_FOLDER="objects";

	/**
	 * <code>Name</code> of the folder where <code>refs</code> data will be stored disk.
	 */
	public static String REFS_FOLDER="refs"; 

	/**
	 * <code>Name</code> of the folder where <code>heads</code> data will be stored disk.
	 */
	public static String HEADS_FOLDER="heads";

	/**
	 * <code>Name</code> of the folder where <code>tags</code> metadata will be stored disk.
	 */
	public static String TAGS_FOLDER="tags";
	
	/**
	 * <code>Name</code> of the folder where <code>tags</code> temporary files will be stored on the disk.
	 */
	public static String TEMP_FOLDER="temp";
	/**
	 * <code>Name</code> of the folder where <code>configuration</code> data will be stored on disk.
	 */
	public static String CONFIG_FOLDER="config";
	
	
	/**
	 * <code>Private</code> so that no one creates its instance.
	 */
	private Constants(){
		//EMPTY
	}
}
