package redis.command;

import redis.command.model.Command;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class PingCommandProcessor implements CommandProcessor {


    @Override
    public void processCommand(List<String> command, OutputStream os) {
        System.out.printf("Processing PING command %s%n", command);
        try {
            os.write(("+" + Command.PONG.getValue() + "\r\n").getBytes());
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
