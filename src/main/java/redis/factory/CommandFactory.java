package redis.factory;

import redis.Command;
import redis.command.CommandProcessor;
import redis.command.EchoCommandProcessor;
import redis.command.PingCommandProcessor;

public class CommandFactory {
    private final String command;

    public CommandFactory(String command) {
        this.command = command;
    }

    public CommandProcessor getInstance(){
        if (command.equalsIgnoreCase(Command.PING.getValue())){
            return new PingCommandProcessor();
        } else if (command.toLowerCase().contains(Command.ECHO.getValue().toLowerCase())) {
            return new EchoCommandProcessor();
        }
        return null;
    }
}
