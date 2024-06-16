package redis.service.master;

public class IdValidator {
    public String validateId(String id, String[] splitId) {
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
}
