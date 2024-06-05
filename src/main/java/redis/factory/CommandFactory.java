package redis.factory;

import redis.command.model.Command;
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
        }else if (command.equals(Command.INFO)){
            return new InfoCommandProcessor();
        } else if (command.equals(Command.REPLCONF)) {
            return new ReplConfCommandProcessor();
        } else if (command.equals(Command.FULLRESYNC)) {
            return new FullResyncCommandProcessor();
        }
        return null;
    }
}
