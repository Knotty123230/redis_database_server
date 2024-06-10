package redis.command.master;

import redis.command.CommandProcessor;
import redis.command.model.Command;
import redis.storage.RedisStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetCommandProcessor implements CommandProcessor {
    private final RedisStorage redisStorage = RedisStorage.getInstance();

    @Override
    public void processCommand(List<String> commands, OutputStream os) {

        System.out.printf("Processing SET master command %s%n", commands);
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
                System.out.println("NumberFormatException:  " + e.getMessage());
            }
        } else {
            redisStorage.save(commands.getFirst().toLowerCase(), value);
        }

        try {
            os.write("+OK\r\n".getBytes());
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
