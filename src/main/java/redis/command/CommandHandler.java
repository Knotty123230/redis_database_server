package redis.command;

import java.io.OutputStream;
import java.util.List;

public interface CommandHandler {
    boolean processCommand(List<String> commands, OutputStream os);
}
