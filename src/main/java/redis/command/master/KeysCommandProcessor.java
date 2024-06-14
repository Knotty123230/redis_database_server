package redis.command.master;

import redis.command.CommandProcessor;
import redis.parser.CommandParser;
import redis.storage.RedisStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class KeysCommandProcessor implements CommandProcessor {
    private final RedisStorage redisStorage;
    private final CommandParser commandParser;

    public KeysCommandProcessor() {
        redisStorage = RedisStorage.getInstance();
        this.commandParser = new CommandParser();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        List<String> keys = redisStorage.getKeys();
        os.write(commandParser.getResponseFromCommandArray(keys).getBytes());
        os.flush();
    }
}
