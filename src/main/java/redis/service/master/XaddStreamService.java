package redis.service.master;

import redis.storage.RedisStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class XaddStreamService {
    private final Map<String, Map<String, String>> stream;
    private static final XaddStreamService streamService = new XaddStreamService();
    private static final Map<String, String> lastId = new HashMap<>();
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
        if (stream.containsKey(name)){
            Map<String, String> storage = stream.get(name);
            String x = validateId(name, id);
            if (x != null) return x;
            lastId.put(name, id);
            storage.put(id, key);
            redisStorage.save(key, value);
            return id;
        }
        HashMap<String, String> storage = new HashMap<>();
        storage.put(id, key);
        redisStorage.save(key,value);
        stream.put(name, storage);
        lastId.put(name, id);
        return id;
    }

    private static String validateId(String name, String id) {
        String lastRecordId = lastId.get(name);
        System.out.println("existId = " + lastRecordId);
        String[] splitId = lastRecordId.split("-");
        Long firsPart = Long.parseLong(splitId[0]);
        Long secondPart = Long.parseLong(splitId[1]);
        String[] currId = id.split("-");
        long currFirstPart = Long.parseLong(currId[0]);
        long currSecondPart = Long.parseLong(currId[1]);
        if (currFirstPart == 0 && currSecondPart == 0)
            return "-ERR The ID specified in XADD must be greater than 0-0\r\n";
        if ((firsPart > currFirstPart || secondPart >= currSecondPart))
            return "-ERR The ID specified in XADD is equal or smaller than the target stream top item\r\n";
        return null;
    }

    public boolean streamExists(String name){
        return stream.containsKey(name);
    }
}
