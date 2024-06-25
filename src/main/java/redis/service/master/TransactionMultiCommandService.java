package redis.service.master;

import redis.model.Transaction;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionMultiCommandService {
    private static volatile TransactionMultiCommandService transactionMultiCommandService;
    private final Map<OutputStream, Transaction> commandsQueue;

    private TransactionMultiCommandService() {
        commandsQueue = new ConcurrentHashMap<>();
    }


    public static TransactionMultiCommandService getInstance() {
        if (transactionMultiCommandService == null) {
            synchronized (TransactionMultiCommandService.class) {
                if (transactionMultiCommandService == null) {
                    transactionMultiCommandService = new TransactionMultiCommandService();
                }
            }
        }
        return transactionMultiCommandService;
    }

    public void addCommandToQueue(OutputStream os, List<String> command) {
        if (commandsQueue.containsKey(os)) {
            commandsQueue.get(os).getQueue().add(command);
        }
    }

    public synchronized void startTransaction(OutputStream outputStream) {
        commandsQueue.put(outputStream, new Transaction(true, true));
    }

    public Transaction getTransaction(OutputStream outputStream) {
        return commandsQueue.get(outputStream);
    }

    public OutputStream getClient(OutputStream os) {
        if (commandsQueue.containsKey(os)) {
            return os;
        }
        return null;
    }


    public Queue<List<String>> getCommandsQueue(OutputStream os) {
        if (commandsQueue.containsKey(os)) {

            return commandsQueue.get(os).getQueue();
        }
        return null;
    }


}
