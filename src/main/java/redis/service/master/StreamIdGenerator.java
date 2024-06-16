package redis.service.master;

public class StreamIdGenerator {
    public String generateFullId() {
        return System.currentTimeMillis() + "-" + "0";
    }

    public String generateId(String id, String lastRecordId) {
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

    public String generateNewId(String id) {
        String[] strings = id.split("-");
        if (Long.parseLong(strings[0]) == 0) {
            return id.substring(0, id.length() - 1) + "1";
        } else {
            return id.substring(0, id.length() - 1) + "0";
        }
    }

    public String generateNextId(String lastRecordId) {
        long l = Long.parseLong(lastRecordId.split("-")[1]) + 1;
        return lastRecordId.substring(0, lastRecordId.length() - 1) + l;
    }
}
