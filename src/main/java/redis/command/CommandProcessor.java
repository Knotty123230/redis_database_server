package redis.command;

import java.io.OutputStream;
import java.util.List;

public interface CommandProcessor {
    void processCommand(List<String> command, OutputStream os);
}
