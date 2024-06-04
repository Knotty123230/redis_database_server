package redis.command;

import redis.command.model.Command;
import redis.storage.RedisStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetCommandProcessor implements CommandProcessor {
    private final RedisStorage redisStorage = RedisStorage.getInstance();

    @Override
    public byte[] processCommand(List<String> commands) {
        System.out.printf("Processing SET command %s%n", commands);
        Map<String, String> commandsMap = new HashMap<>();

        for (int i = 2; i < commands.size(); i += 2) {
            commandsMap.put(commands.get(i).toLowerCase(), commands.get(i + 1));
        }

        String value = commands.get(1);
        String expiration = commandsMap.get(Command.PX.getValue().toLowerCase());

        if (expiration != null) {
            try {
                Long expirationTime = Long.parseLong(expiration);
                redisStorage.save(commands.getFirst().toLowerCase(), value, expirationTime);
            } catch (NumberFormatException e) {
                return "-ERR invalid expiration time\r\n".getBytes();
            }
        } else {
            redisStorage.save(commands.getFirst().toLowerCase(), value);
        }

        return "+OK\r\n".getBytes();
    }
}
