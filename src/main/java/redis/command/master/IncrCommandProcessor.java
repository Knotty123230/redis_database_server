package redis.command.master;

import redis.command.CommandProcessor;
import redis.storage.RedisStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class IncrCommandProcessor implements CommandProcessor {
    private final RedisStorage redisStorage;

    public IncrCommandProcessor() {
        redisStorage = RedisStorage.getInstance();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        String key = command.remove(0);
        int resp = Integer.parseInt(redisStorage.getCommand(key)) + 1;
        os.write((":" + resp + "\r\n").getBytes());
        os.flush();
    }
}
