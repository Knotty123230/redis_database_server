package redis.command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface CommandProcessor {
    void processCommand(List<String> command, OutputStream os) throws IOException;
}
