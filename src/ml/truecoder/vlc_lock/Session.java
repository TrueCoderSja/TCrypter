package ml.truecoder.vlc_lock;

import java.util.HashMap;

public class Session {
	private static HashMap<String, String> database=new HashMap<String, String>();
	
	public static void store(String key, String val) {
		database.put(key, val);
	}
	
	public static String retrieve(String key) {
		return database.get(key);
	}
}
