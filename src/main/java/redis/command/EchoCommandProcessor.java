package redis.command;

import java.util.List;

public class EchoCommandProcessor implements CommandProcessor {
    @Override
    public byte[] processCommand(List<String> command) {
        System.out.printf("Processing ECHO command %s%n", command);
        return ResponseUtil.getResponse(command.get(0));
    }
}
