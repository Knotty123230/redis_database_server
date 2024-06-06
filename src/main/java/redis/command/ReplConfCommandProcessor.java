package redis.command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplConfCommandProcessor implements CommandProcessor {
    @Override
    public void processCommand(List<String> command, OutputStream os) {
        try {
            os.write("+OK\r\n".getBytes());
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
