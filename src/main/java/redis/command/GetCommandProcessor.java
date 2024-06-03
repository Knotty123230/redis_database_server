package redis.command;

import redis.storage.RedisStorage;


public class GetCommandProcessor implements CommandProcessor {
    private final RedisStorage storage = new RedisStorage();
    @Override
    public byte[] processCommand(String command) {
        System.out.printf("Processing GET command %s%n", command);
        String response = storage.getCommand(command);
        return ResponseUtil.getResponse(response);
    }


}
