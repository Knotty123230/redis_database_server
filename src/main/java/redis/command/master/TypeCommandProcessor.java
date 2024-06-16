package redis.command.master;

import redis.command.CommandProcessor;
import redis.command.model.TypeCommand;
import redis.service.master.StreamService;
import redis.storage.RedisStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TypeCommandProcessor implements CommandProcessor {
    private final RedisStorage redisStorage;
    private final StreamService xaddStreamService;

    public TypeCommandProcessor() {
        redisStorage = RedisStorage.getInstance();
        xaddStreamService = StreamService.getInstance();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        System.out.println("Process TYPE command: " + command);

        if (xaddStreamService.streamExists(command.getFirst())){
            os.write(("+stream\r\n").getBytes());
            os.flush();
            return;
        }
        String value = redisStorage.getCommand(command.getFirst());
        if (value == null || value.isEmpty()) {
            os.write(("+" + TypeCommand.NONE.getValue().toLowerCase() + "\r\n").getBytes());
            os.flush();
            return;
        }
        os.write(("+" + TypeCommand.STRING.getValue().toLowerCase() + "\r\n").getBytes());
        os.flush();
    }
}
