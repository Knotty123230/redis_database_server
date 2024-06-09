package redis.factory;

import redis.command.*;
import redis.command.model.Command;
import redis.service.ReplicaSender;

public class CommandFactory implements Factory {
    private final Command command;
    private final ReplicaSender replicaSender;

    public CommandFactory(Command command, ReplicaSender replicaSender) {
        this.command = command;
        this.replicaSender = replicaSender;
    }

    public CommandFactory(Command command) {
        this.command = command;
        this.replicaSender = null;
    }

    public CommandProcessor getInstance() {
        System.out.printf("factory get instance of command %s%n", command);
        if (command.equals(Command.PING)) {
            return replicaSender == null ? new ReplicaPingCommandProcessor() : new PingCommandProcessor();
        } else if (command.equals(Command.ECHO)) {
            return replicaSender == null ? new ReplicaEchoCommandProcessor() : new EchoCommandProcessor();
        } else if (command.equals(Command.SET)) {
            return replicaSender == null ? new ReplicaSetCommandProcessor() : new SetCommandProcessor();
        } else if (command.equals(Command.GET)) {
            return replicaSender == null ? new ReplicaGetCommandProcessor() : new GetCommandProcessor();
        } else if (command.equals(Command.INFO)) {
            return replicaSender == null ? new ReplicaInfoCommandProcessor() : new InfoCommandProcessor();
        } else if (command.equals(Command.REPLCONF)) {
            return replicaSender == null ? new ReplicaReplConfCommandProcessor() : new ReplConfCommandProcessor();
        } else if (command.equals(Command.PSYNC)) {
            return replicaSender == null ? new ReplicaFullResyncCommandProcessor() : new FullResyncCommandProcessor(replicaSender);
        } else if (command.equals(Command.FULLRESYNC)) {
            return new ReplicaFullResyncCommandProcessor();
        }
        return null;
    }
}
