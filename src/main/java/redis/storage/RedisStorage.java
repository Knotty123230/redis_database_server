package redis.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisStorage {
    private static final RedisStorage redisStorage = new RedisStorage();
    private final Map<String, String> storage = new ConcurrentHashMap<>();
    private final Map<String, Long> timeToExpiration = new ConcurrentHashMap<>();
    private final Map<String, Long> currentTimeForKey = new ConcurrentHashMap<>();

    private RedisStorage() {
    }

    public static RedisStorage getInstance() {
        return redisStorage;

    }

    public void save(String key, String value) {
        storage.put(key, value);
    }

    public void save(String key, String value, Long time) {
        System.out.println("SAVE RedisStorage: " + key);
        currentTimeForKey.put(key, System.currentTimeMillis());
        timeToExpiration.put(key, time);
        save(key, value);
    }

    public String getCommand(String key) {
        System.out.println("GET RedisStorage: " + key);
        if (currentTimeForKey.containsKey(key)) {
            System.out.println("Expiration: " + (System.currentTimeMillis() - currentTimeForKey.get(key)));
        }
        if (timeToExpiration.containsKey(key) && (System.currentTimeMillis() - currentTimeForKey.get(key)) > timeToExpiration.get(key)) {
            return "";
        }
        return storage.get(key);
    }
}
