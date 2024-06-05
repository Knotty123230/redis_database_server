package redis.command;

import java.util.List;

public class ReplConfCommandProcessor implements CommandProcessor {
    @Override
    public byte[] processCommand(List<String> command) {
        return "+OK\r\n".getBytes();
    }
}
