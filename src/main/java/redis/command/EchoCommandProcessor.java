package redis.command;

import redis.Command;

public class EchoCommandProcessor implements CommandProcessor {
    @Override
    public byte[] processCommand(String command) {
        int i = command.indexOf(Command.ECHO.getValue());
        return command.substring(i + 1).getBytes();
    }
}
