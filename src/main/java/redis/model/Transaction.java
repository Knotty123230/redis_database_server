package redis.model;

import java.io.OutputStream;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Transaction {
    private final AtomicBoolean atomicBoolean;
    private final AtomicBoolean isDiscard;
    private final AtomicBoolean isMulti;
    private final AtomicReference<OutputStream> client;
    private final Queue<List<String>> queue;

    public Transaction(boolean atomicBool, boolean isMulti) {
        atomicBoolean = new AtomicBoolean(atomicBool);
        isDiscard = new AtomicBoolean(false);
        this.isMulti = new AtomicBoolean(isMulti);
        client = new AtomicReference<>();
        this.queue = new ConcurrentLinkedDeque<>();
    }

    public boolean isDiscard() {
        return isDiscard.get();
    }

    public void stopTransaction() {
        atomicBoolean.set(false);
        isMulti.set(false);
        client.set(null);
    }

    public boolean isTransactionStarted() {
        return atomicBoolean.get();
    }

    public boolean isMulti() {
        return isMulti.get();
    }

    public AtomicBoolean getAtomicBoolean() {
        return atomicBoolean;
    }

    public AtomicBoolean getIsDiscard() {
        return isDiscard;
    }

    public void setIsDiscard(boolean isDiscard) {
        this.isDiscard.set(isDiscard);
    }

    public AtomicBoolean getIsMulti() {
        return isMulti;
    }

    public AtomicReference<OutputStream> getClient() {
        return client;
    }

    public Queue<List<String>> getQueue() {
        return queue;
    }
}
