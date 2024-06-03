package redis.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisStorage {
    private final Map<String, String> storage = new ConcurrentHashMap<>();
    private static final RedisStorage redisStorage = new RedisStorage();
    private RedisStorage(){
    }
    public static RedisStorage getInstance(){
        return redisStorage;
    }

    public void save(String key, String value){
        storage.put(key, value);
    }
    public String getCommand(String key){
        return storage.get(key);
    }
}
