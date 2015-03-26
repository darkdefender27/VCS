package network;

import java.util.HashMap;

public class HashUtil {

    private static final HashMap<String, String> map = new HashMap<>();
    private static HashUtil instance = new HashUtil();

    private HashUtil() {
    }

    public static HashUtil getInstance() {
        return instance;
    }

    public static String getValue(String key) {
        return map.get(key);
    }

    public static void add(String[][] pairs) {
        for(String[] pair : pairs) {
            map.put(pair[0], pair[1]);
        }
    }

    public static void add(String[] keys, String[] values) {
        for (int i = 0; i < keys.length; ++i) {
            map.put(keys[i], values[i]);
        }
    }
    
    public static void add(String key, String value) {
            map.put(key, value);
    }
}
