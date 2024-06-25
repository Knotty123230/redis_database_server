package redis.model;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class Transaction {
    private final AtomicBoolean atomicBoolean;
    private final AtomicBoolean isDiscard;
    private final AtomicBoolean isMulti;
    private final Queue<List<String>> queue;

    public Transaction(boolean atomicBool, boolean isMulti) {
        atomicBoolean = new AtomicBoolean(atomicBool);
        isDiscard = new AtomicBoolean(false);
        this.isMulti = new AtomicBoolean(isMulti);
        this.queue = new ConcurrentLinkedDeque<>();
    }

    public boolean isDiscard() {
        return isDiscard.get();
    }

    public void stopTransaction() {
        atomicBoolean.set(false);
        isMulti.set(false);
    }

    public boolean isTransactionStarted() {
        return atomicBoolean.get();
    }

    public boolean isMulti() {
        return isMulti.get();
    }


    public void setIsDiscard(boolean isDiscard) {
        this.isDiscard.set(isDiscard);
    }


    public Queue<List<String>> getQueue() {
        return queue;
    }
}
