package redis.factory.replica;

import redis.command.CommandProcessor;
import redis.command.model.Command;
import redis.command.replica.*;
import redis.factory.Factory;

public class ReplicaCommandFactory implements Factory {
    private final Command command;

    public ReplicaCommandFactory(Command command) {
        this.command = command;
    }

    @Override
    public CommandProcessor getInstance() {
        System.out.println("ReplicaCommandFactory: " + command.getValue());
        if (command.equals(Command.PING)) {
            return new ReplicaPingCommandProcessor();
        } else if (command.equals(Command.ECHO)) {
            return new ReplicaEchoCommandProcessor();
        } else if (command.equals(Command.SET)) {
            return new ReplicaSetCommandProcessor();
        } else if (command.equals(Command.GET)) {
            return new ReplicaGetCommandProcessor();
        } else if (command.equals(Command.INFO)) {
            return new ReplicaEchoCommandProcessor();
        } else if (command.equals(Command.REPLCONF)) {
            return new ReplicaReplConfCommandProcessor();
        } else if (command.equals(Command.PSYNC)) {
            return new ReplicaFullResyncCommandProcessor();
        } else if (command.equals(Command.FULLRESYNC)) {
            return new ReplicaFullResyncCommandProcessor();
        }
        return null;
    }
}
