package redis.command;

import redis.storage.RedisStorage;

import java.util.Map;

public class SetCommandProcessor implements CommandProcessor {
    private final RedisStorage redisStorage = new RedisStorage();
    @Override
    public byte[] processCommand(String command) {
        System.out.printf("Processing SET command %s%n", command);
        String[] keyValue = validateCommand(command);
        redisStorage.save(keyValue[0], keyValue[1]);
        return "+OK\r\n".getBytes();
    }

    private String[] validateCommand(String command) {
        return command.split(" ");
    }
}
