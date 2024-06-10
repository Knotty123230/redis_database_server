package redis.command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WaitCommandProcessor implements CommandProcessor {
    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        int timeout = Integer.parseInt(command.getFirst());

        // Process the wait command logic here

        // Write the integer response to the output stream
        String response = ":" + timeout + "\r\n";
        os.write(response.getBytes());
        os.flush();
    }
}
