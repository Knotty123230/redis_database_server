package redis.factory.master;

import redis.command.CommandProcessor;
import redis.command.WaitCommandProcessor;
import redis.command.master.*;
import redis.command.model.Command;
import redis.factory.Factory;
import redis.service.master.ReplicaReceiver;
import redis.service.master.ReplicaSender;

public class MasterCommandFactory implements Factory {
    private final Command command;
    private final ReplicaSender replicaSender;
    private final ReplicaReceiver replicaReceiver;

    public MasterCommandFactory(Command command, ReplicaSender replicaSender, ReplicaReceiver replicaReceiver) {
        this.command = command;
        this.replicaSender = replicaSender;
        this.replicaReceiver = replicaReceiver;
    }

    public CommandProcessor getInstance() {
        System.out.printf("factory get instance of master command %s%n", command);
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
            return new ReplConfCommandProcessor(replicaReceiver);
        } else if (command.equals(Command.PSYNC)) {
            return new FullResyncCommandProcessor(replicaSender);
        } else if (command.equals(Command.WAIT)) {
            return new WaitCommandProcessor(replicaSender, replicaReceiver);
        } else if (command.equals(Command.CONFIG)) {
            return new ConfigCommandProcessor();
        } else if (command.equals(Command.KEYS)) {
            return new KeysCommandProcessor();
        }
        return null;
    }
}
