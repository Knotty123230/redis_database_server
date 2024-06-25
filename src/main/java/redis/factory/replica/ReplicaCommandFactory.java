package redis.factory.replica;

import redis.command.CommandProcessor;
import redis.command.replica.ReplicaFullResyncCommandProcessor;
import redis.command.replica.ReplicaReplConfCommandProcessor;
import redis.command.replica.ReplicaSetCommandProcessor;
import redis.factory.Factory;
import redis.model.Command;

public class ReplicaCommandFactory implements Factory {
    private final Command command;

    public ReplicaCommandFactory(Command command) {
        this.command = command;
    }

    @Override
    public CommandProcessor getInstance() {
        System.out.println("ReplicaCommandFactory: " + command.getValue());
        if (command.equals(Command.SET)) {
            return new ReplicaSetCommandProcessor();
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
