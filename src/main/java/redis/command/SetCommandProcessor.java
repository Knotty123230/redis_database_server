package redis.command;

import redis.storage.RedisStorage;

import java.util.List;

public class SetCommandProcessor implements CommandProcessor {
    private final RedisStorage redisStorage = RedisStorage.getInstance();

    @Override
    public byte[] processCommand(List<String> commands) {
        System.out.printf("Processing SET command %s%n", commands);
        redisStorage.save(commands.get(0), commands.get(1));
        return "+OK\r\n".getBytes();
    }

    private String[] validateCommand(String command) {
        return command.split(" ");
    }
}
