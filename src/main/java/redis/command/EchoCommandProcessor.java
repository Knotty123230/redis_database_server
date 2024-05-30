package redis.command;

import redis.Command;

public class EchoCommandProcessor implements CommandProcessor {
    @Override
    public byte[] processCommand(String command) {
        return ("$" + command.length() + command + "\r\n").getBytes();
    }
}
