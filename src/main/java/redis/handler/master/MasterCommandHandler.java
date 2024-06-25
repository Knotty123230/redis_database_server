package redis.handler.master;

import redis.command.CommandHandler;
import redis.command.CommandProcessor;
import redis.factory.Factory;
import redis.factory.master.MasterCommandFactory;
import redis.model.Command;
import redis.parser.CommandParser;
import redis.service.master.ReplicaReceiver;
import redis.service.master.ReplicaSender;
import redis.service.master.TransactionMultiCommandService;
import redis.utils.CommandUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MasterCommandHandler implements CommandHandler {
    private final ReplicaSender replicaSender;
    private final CommandParser commandParser;
    private final List<String> commands;
    private final OutputStream os;
    private final ReplicaReceiver replicaReceiver;
    private final TransactionMultiCommandService transactionMultiCommandService;

    public MasterCommandHandler(ReplicaReceiver replicaReceiver, ReplicaSender replicaSender, List<String> commands, OutputStream outputStream) {
        this.replicaSender = replicaSender;
        this.commands = commands;
        this.os = outputStream;
        replicaSender.start();
        this.commandParser = new CommandParser();
        this.replicaReceiver = replicaReceiver;
        transactionMultiCommandService = TransactionMultiCommandService.getInstance();
    }



    public synchronized boolean processCommand() {
        ArrayList<String> replicaCommand = new ArrayList<>(commands);

        String remove = commands.removeFirst();
        Command command = CommandUtil.getCommand(remove);
        if (command == null) return true;
        if (addCommandToQueue(command)) return true;
        Factory commandFactory = new MasterCommandFactory(command, replicaSender, replicaReceiver, transactionMultiCommandService);
        CommandProcessor commandProcessor = commandFactory.getInstance();
        try {
            commandProcessor.processCommand(commands, os);
            replicaSender.addCommand(commandParser.getResponseFromCommandArray(replicaCommand));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean addCommandToQueue(Command command) {
        if (transactionMultiCommandService.isTransactionStarted() && !Objects.equals(command, Command.EXEC)){
            transactionMultiCommandService.addCommandToQueue(commands);
            try {
                os.write("+QUEUED\r\n".getBytes());
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        processCommand();
    }
}
