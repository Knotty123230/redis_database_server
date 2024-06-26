package redis.factory.master;

import redis.command.CommandProcessor;
import redis.command.master.*;
import redis.factory.Factory;
import redis.model.Command;
import redis.service.master.ReplicaReceiver;
import redis.service.master.ReplicaSender;
import redis.service.master.TransactionMultiCommandService;

public class MasterCommandFactory implements Factory {
    private final Command command;
    private final ReplicaSender replicaSender;
    private final ReplicaReceiver replicaReceiver;
    private final TransactionMultiCommandService transactionMultiCommandService;

    public MasterCommandFactory(Command command, ReplicaSender replicaSender, ReplicaReceiver replicaReceiver, TransactionMultiCommandService transactionMultiCommandService) {
        this.command = command;
        this.replicaSender = replicaSender;
        this.replicaReceiver = replicaReceiver;
        this.transactionMultiCommandService = transactionMultiCommandService;
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
        } else if (command.equals(Command.TYPE)) {
            return new TypeCommandProcessor();
        } else if (command.equals(Command.XADD)) {
            return new XaddCommandProcessor();
        } else if (command.equals(Command.XRANGE)) {
            return new XrangeCommandProcessor();
        } else if (command.equals(Command.XREAD)) {
            return new XreadCommandProcessor();
        } else if (command.equals(Command.INCR)) {
            return new IncrCommandProcessor();
        } else if (command.equals(Command.MULTI)) {
            return new MultiCommandProcessor(transactionMultiCommandService);
        } else if (command.equals(Command.EXEC)) {
            return new ExecCommandProcessor(transactionMultiCommandService);
        } else if (command.equals(Command.DISCARD)) {
            return new DiscardCommandProcessor(transactionMultiCommandService);
        }
        return null;
    }
}
