package logger;

public class VCSLogger {
	public static void debugLogToCmd(String TAG,String message)
	{
		//System.out.println(TAG + " : " + message);
	}
	public static void infoLogToCmd(String message)
	{
		System.out.println("Info -> " + message);
	}
	public static void errorLogToCmd(String TAG,String message){
		//debug
		System.out.println("Error -> " + TAG + " : " + message);
	}
}
