package redis.service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ReplicaReceiver {
    private final AtomicInteger atomicInteger;
    private final AtomicBoolean atomicBoolean;


    public ReplicaReceiver() {
        synchronized (this) {
            atomicBoolean = new AtomicBoolean(false);
            atomicInteger = new AtomicInteger(0);
        }
    }


    public void receive() {
        atomicInteger.getAndIncrement();
    }

    public int getReceivedCount() {
        return atomicInteger.get();
    }


    public void reset() {
        System.out.println("reset values");
        synchronized (this) {
            atomicBoolean.set(false);
            atomicInteger.set(0);
        }
    }

}
