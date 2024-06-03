package redis.command;

import redis.storage.RedisStorage;

import java.util.List;


public class GetCommandProcessor implements CommandProcessor {
    private final RedisStorage storage = RedisStorage.getInstance();

    @Override
    public byte[] processCommand(List<String> commands) {
        System.out.printf("Processing GET command %s%n", commands);
        String response = storage.getCommand(commands.get(0));
        return ResponseUtil.getResponse(response);
    }


}