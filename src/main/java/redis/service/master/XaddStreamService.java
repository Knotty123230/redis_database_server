package redis.service.master;

import redis.storage.RedisStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class XaddStreamService {
    private final Map<String, Map<String, String>> stream;
    private static final XaddStreamService streamService = new XaddStreamService();
    private static final Map<String, String> lastId = new HashMap<>();
    private static final Map<String, String> autoGenerateIds = new HashMap<>();
    private static final Map<String, String> autoGeneratedFullIds = new HashMap<>();
    private final RedisStorage redisStorage;


    private XaddStreamService() {
        stream = new HashMap<>();
        redisStorage = RedisStorage.getInstance();
    }

    public static XaddStreamService getInstance() {
        return streamService;
    }

    public String createStream(String name, String id, String key, String value) {
        System.out.println("CREATE STREAM: " + name + " " + id + " " + key + " " + value);

        if (stream.containsKey(name)) {
            return handleExistingStream(name, id, key, value);
        } else {
            return handleNewStream(name, id, key, value);
        }
    }

    private String handleExistingStream(String name, String id, String key, String value) {
        if (autoGeneratedFullIds.containsKey(name)){
            String authogeneratedFullID = autoGeneratedFullIds.get(name);
            long lastId = Long.parseLong(authogeneratedFullID.substring(authogeneratedFullID.length() - 1)) + 1;
            String generatedNewFullId = authogeneratedFullID.substring(0, authogeneratedFullID.length() - 1) + lastId;
            autoGeneratedFullIds.put(name, generatedNewFullId);
            return generatedNewFullId;
        }
        if (autoGenerateIds.containsKey(name)) {
            id = generateId(id, autoGenerateIds.get(name));
            autoGenerateIds.put(name, id);
            return id;
        }

        Map<String, String> storage = stream.get(name);
        String lastRecordId = lastId.get(name);
        System.out.println("existId = " + lastRecordId);
        String[] splitId = lastRecordId.split("-");
        String validationError = validateId(id, splitId);

        if (validationError != null) {
            return validationError;
        }

        lastId.put(name, id);
        storage.put(id, key);
        redisStorage.save(key, value);
        return id;
    }

    private String handleNewStream(String name, String id, String key, String value) {
        HashMap<String, String> storage = new HashMap<>();
        if (id.length() == 1){
            id = generateFullId(name, "");
            autoGeneratedFullIds.put(name, id);
        }
        if (id.endsWith("*")) {
            id = generateId(id, "");
            autoGenerateIds.put(name, id);
        }

        storage.put(id, key);
        redisStorage.save(key, value);
        stream.put(name, storage);
        lastId.put(name, id);
        return id;
    }

    private String generateFullId(String name, String s) {
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 13; i++) {
            id.append(random.nextInt(9));
        }
        id.append("-").append("0");
        return id.toString();
    }

    private String generateId(String id, String lastRecordId) {
        String[] strings = id.split("-");
        String string = strings[1];

        if (string.equals("*")) {
            if (lastRecordId.isEmpty()) {
                return generateNewId(id);
            }

            if (lastRecordId.split("-")[0].equals(id.split("-")[0])) {
                return generateNextId(lastRecordId);
            } else {
                return generateNewId(id);
            }
        }
        return "";
    }

    private String generateNewId(String id) {
        String[] strings = id.split("-");
        if (Long.parseLong(strings[0]) == 0) {
            return id.substring(0, id.length() - 1) + "1";
        } else {
            return id.substring(0, id.length() - 1) + "0";
        }
    }

    private String generateNextId(String lastRecordId) {
        long l = Long.parseLong(lastRecordId.split("-")[1]) + 1;
        return lastRecordId.substring(0, lastRecordId.length() - 1) + l;
    }

    private String validateId(String id, String[] splitId) {
        long firstPart = Long.parseLong(splitId[0]);
        long secondPart = Long.parseLong(splitId[1]);
        String[] currId = id.split("-");
        long currFirstPart = Long.parseLong(currId[0]);
        long currSecondPart = Long.parseLong(currId[1]);

        if (currFirstPart == 0 && currSecondPart == 0) {
            return "-ERR The ID specified in XADD must be greater than 0-0\r\n";
        }

        if (firstPart > currFirstPart || (firstPart == currFirstPart && secondPart >= currSecondPart)) {
            return "-ERR The ID specified in XADD is equal or smaller than the target stream top item\r\n";
        }

        return null;
    }

    public boolean streamExists(String name) {
        return stream.containsKey(name);
    }
}
