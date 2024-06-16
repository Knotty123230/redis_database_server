package redis.service.master;

import redis.storage.RedisStorage;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<Map<String, List<String>>> findValuesByStreamName(String name, List<String> streamKeys) {
        return findValues(name, streamKeys, ValueCondition.GREATER_OR_EQUAL);
    }

    public List<Map<String, List<String>>> findValuesNotBiggerThenId(String name, List<String> streamKeys) {
        return findValues(name, streamKeys, ValueCondition.LESS_OR_EQUAL);
    }

    public List<Map<String, List<String>>> findValuesBiggerThenId(String name, List<String> streamKeys) {
        return findValues(name, streamKeys, ValueCondition.GREATER);
    }

    private enum ValueCondition {
        GREATER, LESS_OR_EQUAL, GREATER_OR_EQUAL
    }

    private List<Map<String, List<String>>> findValues(String name, List<String> streamKeys, ValueCondition condition) {
        List<Map<String, List<String>>> result = new ArrayList<>();
        Set<String> ids = getFirstPartOfId(streamKeys);
        String comparisonValue = condition == ValueCondition.LESS_OR_EQUAL ? getMaxValue(streamKeys) : getMinValue(streamKeys);
        Map<String, String> existsIdAndKey = stream.get(name);

        if (existsIdAndKey != null) {
            for (Map.Entry<String, String> entry : existsIdAndKey.entrySet()) {
                String[] split = entry.getKey().split("-");
                String existsKey = split[0];
                if (ids.contains(existsKey)) {
                    long entryId = Long.parseLong(split[1]);
                    long comparisonId = Long.parseLong(comparisonValue);
                    if (shouldSkip(entryId, comparisonId, condition)) continue;
                    result.add(createEntryMap(entry));
                }
            }
        }
        return result;
    }

    private boolean shouldSkip(long entryId, long comparisonId, ValueCondition condition) {
        return switch (condition) {
            case GREATER, GREATER_OR_EQUAL -> entryId < comparisonId;
            case LESS_OR_EQUAL -> entryId > comparisonId;
            default -> false;
        };
    }

    private Map<String, List<String>> createEntryMap(Map.Entry<String, String> entry) {
        String key = entry.getValue();
        String value = redisStorage.getCommand(key);
        List<String> keyValueList = new ArrayList<>();
        keyValueList.add(key);
        keyValueList.add(value);
        Map<String, List<String>> entryMap = new HashMap<>();
        entryMap.put(entry.getKey(), keyValueList);
        return entryMap;
    }


    private String getMaxValue(List<String> streamKeys) {
        return streamKeys
                .stream()
                .map(it -> it.split("-"))
                .map(it -> it[1])
                .max(Comparator.naturalOrder())
                .orElseThrow();
    }

    private static Set<String> getFirstPartOfId(List<String> streamKeys) {
        return streamKeys
                .stream()
                .map(it -> it.split("-")[0])
                .collect(Collectors.toSet());
    }

    private static String getMinValue(List<String> streamKeys) {
        return streamKeys.stream()
                .map(it -> it.split("-"))
                .map(it -> it[1])
                .sorted()
                .findFirst()
                .orElseThrow();
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
        Map<String, String> storage = stream.get(name);
        if (autoGeneratedFullIds.containsKey(name)) {
            String authogeneratedFullID = autoGeneratedFullIds.get(name);
            long lastId = Long.parseLong(authogeneratedFullID.substring(authogeneratedFullID.length() - 1)) + 1;
            String generatedNewFullId = authogeneratedFullID.substring(0, authogeneratedFullID.length() - 1) + lastId;
            autoGeneratedFullIds.put(name, generatedNewFullId);
            storage.put(generatedNewFullId, key);
            redisStorage.save(key, value);
            return generatedNewFullId;
        }
        if (autoGenerateIds.containsKey(name)) {
            id = generateId(id, autoGenerateIds.get(name));
            autoGenerateIds.put(name, id);
            storage.put(id, key);
            redisStorage.save(key, value);
            return id;
        }

        String lastRecordId = lastId.get(name);
        System.out.println("existId = " + lastRecordId);
        String[] splitId = lastRecordId.split("-");
        String validationError = validateId(id, splitId);

        if (validationError != null) {
            return validationError;
        }

        storage.put(id, key);
        lastId.put(name, id);
        redisStorage.save(key, value);
        return id;
    }

    private String handleNewStream(String name, String id, String key, String value) {
        HashMap<String, String> storage = new HashMap<>();
        if (id.length() == 1) {
            id = generateFullId();
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

    private String generateFullId() {
        StringBuilder id = new StringBuilder();
        id.append(System.currentTimeMillis()).append("-").append("0");
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
