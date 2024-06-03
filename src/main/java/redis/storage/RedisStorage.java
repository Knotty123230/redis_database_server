package redis.storage;

import java.util.HashMap;
import java.util.Map;

public class RedisStorage {
    private final Map<String, String> storage = new HashMap<>();
    public void save(String key, String value){
        storage.put(key, value);
    }
    public String getCommand(String key){
        return storage.get(key);
    }
}
