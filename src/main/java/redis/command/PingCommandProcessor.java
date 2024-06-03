package redis.command;

import redis.Command;

import java.util.List;

public class PingCommandProcessor implements CommandProcessor {


    @Override
    public byte[] processCommand(List<String> command) {
        System.out.printf("Processing PING command %s%n", command);
        return ("+" + Command.PONG.getValue() + "\r\n").getBytes();
    }
}
