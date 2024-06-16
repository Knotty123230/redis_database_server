package redis.service.master;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExpireCommand {
    private final String id;
    private final AtomicBoolean isReady;
    private final AtomicBoolean isAdded;


    public ExpireCommand() {
        this.isReady = new AtomicBoolean(false);
        this.id = "";
        this.isAdded = new AtomicBoolean(false);
    }

    public void isAdded(boolean isAdded) {
        if (isReady.get()) {
            this.isAdded.set(isAdded);
        }
    }

    public boolean getIsAdded() {
        return this.isAdded.get();
    }

    public void setReady(boolean ready) {
        this.isReady.set(ready);
    }

    public String getId() {
        return id;
    }
}
