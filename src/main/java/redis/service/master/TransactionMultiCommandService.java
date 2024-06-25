package redis.service.master;

import java.io.OutputStream;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionMultiCommandService {
    private static volatile TransactionMultiCommandService transactionMultiCommandService;
    private final AtomicBoolean atomicBoolean;
    private final Queue<List<String>> commandsQueue;
    private final AtomicBoolean isMulti;
    private final AtomicReference<OutputStream> client;

    private TransactionMultiCommandService() {
        commandsQueue = new ConcurrentLinkedDeque<>();
        atomicBoolean = new AtomicBoolean(false);
        isMulti = new AtomicBoolean(false);
        client = new AtomicReference<>();
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

    public void addCommandToQueue(List<String> command) {
        commandsQueue.add(command);
    }

    public void stopTransaction() {
        atomicBoolean.set(false);
        isMulti.set(false);
        client.set(null);
    }


    public void startTransaction(OutputStream outputStream) {
        atomicBoolean.set(true);
        isMulti.set(true);
        client.set(outputStream);
    }

    public OutputStream getClient() {
        return client.get();
    }

    public boolean isTransactionStarted() {
        return atomicBoolean.get();
    }


    public Queue<List<String>> getCommandsQueue() {
        return commandsQueue;
    }

    public boolean isMulti() {
        return isMulti.get();
    }
}
