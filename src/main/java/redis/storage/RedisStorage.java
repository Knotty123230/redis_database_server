package redis.storage;

import redis.service.master.RdbFileReader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisStorage {
    private static final RedisStorage redisStorage = new RedisStorage();
    private final Map<String, String> storage = new ConcurrentHashMap<>();
    private final Map<String, Long> timeToExpiration = new ConcurrentHashMap<>();
    private final Map<String, Long> currentTimeForKey = new ConcurrentHashMap<>();

    private RedisStorage() {
        RdbFileReader reader = new RdbFileReader();
        Map<String, String> stringStringMap = reader.readFile();
        storage.putAll(stringStringMap);
        Map<String, Long> keysExpiration = reader.getKeysExpiration();
        timeToExpiration.putAll(keysExpiration);
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

        if (timeToExpiration.containsKey(key) && !currentTimeForKey.containsKey(key)) {
            long currentTime = System.currentTimeMillis();
            long expirationTime = timeToExpiration.get(key);
            if (currentTime > expirationTime) {
                System.out.println("Key expired: " + key);
                return "";
            }
        }
        if (currentTimeForKey.containsKey(key)) {
            long currTime = System.currentTimeMillis();
            long expirationTime = timeToExpiration.get(key);
            long currExpiration = currentTimeForKey.get(key);
            if (currTime - currExpiration > expirationTime) {
                return "";
            }
        }

        return storage.get(key);
    }

    public List<String> getKeys() {
        return storage.keySet().stream().toList();
    }
}
