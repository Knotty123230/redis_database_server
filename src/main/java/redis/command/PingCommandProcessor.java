package redis.command;

import redis.Command;

public class PingCommandProcessor implements CommandProcessor {


    @Override
    public byte[] processCommand(String command) {
        return ("+" + Command.PONG.getValue() + "\r\n").getBytes();
    }
}
