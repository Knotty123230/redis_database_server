package redis.command;

import java.util.List;

public interface CommandProcessor {
    byte[] processCommand(List<String> command);
}
