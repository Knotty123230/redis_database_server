package redis.command;

import redis.utils.ResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class EchoCommandProcessor implements CommandProcessor {
    @Override
    public void processCommand(List<String> command, OutputStream os) {
        System.out.printf("Processing ECHO command %s%n", command);
        try {
            os.write(ResponseUtil.getResponse(command.get(0)));
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
