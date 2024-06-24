package redis.command.master;

import redis.command.CommandProcessor;
import redis.storage.RedisStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class IncrCommandProcessor implements CommandProcessor {
    private final RedisStorage redisStorage;
    private Integer increment;

    public IncrCommandProcessor() {
        redisStorage = RedisStorage.getInstance();
        this.increment = 1;
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        String key = command.remove(0);
        String value = redisStorage.getCommand(key);
        if (value == null) {
            os.write((":" + increment + "\r\n").getBytes());
            os.flush();
            redisStorage.save(key, String.valueOf(increment));
            return;
        }
        int resp = Integer.parseInt(value) + 1;

        os.write((":" + resp + "\r\n").getBytes());
        os.flush();
        redisStorage.save(key, String.valueOf(resp));
    }
}
