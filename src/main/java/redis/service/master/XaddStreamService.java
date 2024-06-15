package redis.service.master;

import redis.storage.RedisStorage;

import java.util.HashMap;
import java.util.Map;

public class XaddStreamService {
    private final Map<String, Map<String, String>> stream;
    private static final XaddStreamService streamService = new XaddStreamService();
    private final RedisStorage redisStorage;


    private XaddStreamService() {
        stream = new HashMap<>();
        redisStorage = RedisStorage.getInstance();
    }
    public static XaddStreamService getInstance(){
        return streamService;
    }
    public String createStream(String name, String id, String key, String value){
        System.out.println("CREATE STREAM: " + name + " " + id + " "+ key + " " + value);
        if (stream.containsKey(id)){
            Map<String, String> storage = stream.get(id);
            storage.put(id, key);
            redisStorage.save(key, value);
            return id;
        }
        HashMap<String, String> storage = new HashMap<>();
        storage.put(id, key);
        redisStorage.save(key,value);
        stream.put(name, storage);
        return id;
    }
    public boolean streamExists(String name){
        return stream.containsKey(name);
    }
}
