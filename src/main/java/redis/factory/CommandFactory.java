package redis.factory;

import redis.Command;
import redis.command.CommandProcessor;
import redis.command.EchoCommandProcessor;
import redis.command.PingCommandProcessor;

public class CommandFactory {
    private final Command command;

    public CommandFactory(Command command) {
        this.command = command;
    }

    public CommandProcessor getInstance(){
        System.out.println("factory get instanse of command %s".formatted(command));
        if (command.equals(Command.PING)){
            return new PingCommandProcessor();
        } else if (command.equals(Command.ECHO)) {
            return new EchoCommandProcessor();
        }
        return null;
    }
}
