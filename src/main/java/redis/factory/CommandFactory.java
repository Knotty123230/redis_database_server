package redis.factory;

import redis.Command;
import redis.command.*;

public class CommandFactory {
    private final Command command;

    public CommandFactory(Command command) {
        this.command = command;
    }

    public CommandProcessor getInstance() {
        System.out.printf("factory get instance of command %s%n", command);
        if (command.equals(Command.PING)) {
            return new PingCommandProcessor();
        } else if (command.equals(Command.ECHO)) {
            return new EchoCommandProcessor();
        } else if (command.equals(Command.SET)) {
            return new SetCommandProcessor();
        } else if (command.equals(Command.GET)) {
            return new GetCommandProcessor();
        }
        return null;
    }
}
