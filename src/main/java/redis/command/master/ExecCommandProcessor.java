package redis.command.master;

import redis.command.CommandHandler;
import redis.command.CommandProcessor;
import redis.handler.master.MasterCommandHandler;
import redis.service.master.ReplicaReceiver;
import redis.service.master.ReplicaSender;
import redis.service.master.TransactionMultiCommandService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Queue;

public class ExecCommandProcessor implements CommandProcessor {
    private final TransactionMultiCommandService transactionMultiCommandService;

    public ExecCommandProcessor(TransactionMultiCommandService transactionMultiCommandService) {
        this.transactionMultiCommandService = transactionMultiCommandService;
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        transactionMultiCommandService.stopTransaction();
        Queue<List<String>> commandsQueue = transactionMultiCommandService.getCommandsQueue();
        while (!commandsQueue.isEmpty()) {
            List<String> commands = commandsQueue.poll();
            CommandHandler commandHandler = new MasterCommandHandler(new ReplicaReceiver(), ReplicaSender.getInstance(), commands, os);
            Thread thread = new Thread(commandHandler);
            thread.start();
        }

    }
}
