package redis.command;

import redis.Command;

public class PingCommandProcessor implements CommandProcessor {


    @Override
    public byte[] processCommand(String command) {
        System.out.printf("Processing PING command %s%n", command);
        return ("+" + Command.PONG.getValue() + "\r\n").getBytes();
    }
}
