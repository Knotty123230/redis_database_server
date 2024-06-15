package redis.command.model.counter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandByteCounter {
    private static volatile CommandByteCounter instance;
    private final AtomicInteger bytes;
    private final AtomicBoolean isFirst;

    private CommandByteCounter() {
        bytes = new AtomicInteger(0);
        isFirst = new AtomicBoolean(true);
    }

    public static CommandByteCounter getInstance() {
        if (instance == null) {
            synchronized (CommandByteCounter.class) {
                if (instance == null) {
                    instance = new CommandByteCounter();
                }
            }
        }
        return instance;
    }

    public void setIsFirst(boolean b) {
        isFirst.set(b);
    }

    public boolean isFirst() {
        return isFirst.get();
    }


    public void addBytes(int bytesLength) {
        bytes.addAndGet(bytesLength);
    }

    public void renewBytes(int bytes) {
        this.bytes.set(this.bytes.get() + bytes);
    }

    public Integer getBytes() {
        return bytes.get();
    }
}