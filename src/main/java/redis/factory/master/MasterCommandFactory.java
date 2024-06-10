package redis.factory.master;

import redis.command.*;
import redis.command.master.*;
import redis.command.model.Command;
import redis.command.replica.ReplicaFullResyncCommandProcessor;
import redis.factory.Factory;
import redis.service.ReplicaSender;

public class MasterCommandFactory implements Factory {
    private final Command command;
    private final ReplicaSender replicaSender;

    public MasterCommandFactory(Command command, ReplicaSender replicaSender) {
        this.command = command;
        this.replicaSender = replicaSender;
    }

    public MasterCommandFactory(Command command) {
        this.command = command;
        this.replicaSender = null;
    }

    public CommandProcessor getInstance() {
        System.out.printf("factory get instance of master command %s%n", command);
        if (command.equals(Command.PING)) {
            return  new PingCommandProcessor();
        } else if (command.equals(Command.ECHO)) {
            return new EchoCommandProcessor();
        } else if (command.equals(Command.SET)) {
            return  new SetCommandProcessor();
        } else if (command.equals(Command.GET)) {
            return  new GetCommandProcessor();
        } else if (command.equals(Command.INFO)) {
            return  new InfoCommandProcessor();
        } else if (command.equals(Command.REPLCONF)) {
            return  new ReplConfCommandProcessor();
        } else if (command.equals(Command.PSYNC)) {
            return  new FullResyncCommandProcessor(replicaSender);
        } else if (command.equals(Command.WAIT)) {
            return new WaitCommandProcessor();
        }
        return null;
    }
}
