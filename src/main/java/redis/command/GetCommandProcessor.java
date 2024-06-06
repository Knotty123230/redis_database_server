package redis.command;

import redis.storage.RedisStorage;
import redis.utils.ResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class GetCommandProcessor implements CommandProcessor {
    private final RedisStorage storage = RedisStorage.getInstance();

    @Override
    public void processCommand(List<String> commands, OutputStream os) {
        System.out.printf("Processing GET command %s%n", commands);
        try {
            String response = storage.getCommand(commands.getFirst().toLowerCase());
            if (response.isEmpty() || response.isBlank()) {
                os.write("$-1\r\n".getBytes());
                os.flush();
                return;
            }
            os.write(ResponseUtil.getResponse(response));
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
