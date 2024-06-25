package redis.service.master;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionMultiCommandService {
    private static volatile TransactionMultiCommandService transactionMultiCommandService;
    private final AtomicBoolean atomicBoolean;
    private final Queue<List<String>> commandsQueue;

    private TransactionMultiCommandService() {
        commandsQueue = new ConcurrentLinkedDeque<>();
        atomicBoolean = new AtomicBoolean(false);
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
    }

    public void startTransaction() {
        atomicBoolean.set(true);
    }

    public boolean isTransactionStarted() {
        return atomicBoolean.get();
    }

    public Queue<List<String>> getCommandsQueue() {
        return commandsQueue;
    }
}
