package redis.command.master;

import redis.command.CommandHandler;
import redis.command.CommandProcessor;
import redis.handler.master.MasterCommandHandler;
import redis.model.Transaction;
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
        Transaction transaction = transactionMultiCommandService.getTransaction(os);
        if (transaction == null) {
            os.write("-ERR EXEC without MULTI\r\n".getBytes());
            os.flush();
            return;
        }
        boolean multi = transaction.isMulti();
        System.out.println("MULTI: " + multi);
        if (!multi) {
            os.write("-ERR EXEC without MULTI\r\n".getBytes());
            os.flush();
            return;
        }
        transaction.stopTransaction();
        if (transaction.isDiscard()) {
            return;
        }
        Queue<List<String>> commandsQueue = transactionMultiCommandService.getCommandsQueue(os);

        if (commandsQueue == null || commandsQueue.isEmpty()) {
            os.write(("*" + 0 + "\r\n").getBytes());
            os.flush();
            return;
        }
        System.out.println("COMMANDS QUEUE: " + commandsQueue);
        os.write(("*" + commandsQueue.size() + "\r\n").getBytes());
        while (!commandsQueue.isEmpty()) {
            List<String> commands = commandsQueue.poll();
            CommandHandler commandHandler = new MasterCommandHandler(new ReplicaReceiver(), ReplicaSender.getInstance(), commands, os);
            commandHandler.processCommand();
        }
    }
}
