package redis.factory;

import redis.command.*;
import redis.command.model.Command;
import redis.service.ReplicaSender;

public class CommandFactory {
    private final Command command;
    private final ReplicaSender replicaSender;

    public CommandFactory(Command command, ReplicaSender replicaSender) {
        this.command = command;
        this.replicaSender = replicaSender;
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
        } else if (command.equals(Command.INFO)) {
            return new InfoCommandProcessor();
        } else if (command.equals(Command.REPLCONF)) {
            return new ReplConfCommandProcessor();
        } else if (command.equals(Command.PSYNC)) {
            return new FullResyncCommandProcessor(replicaSender);
        }
        return null;
    }
}
