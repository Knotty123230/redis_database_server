package redis.model;

import java.io.OutputStream;

public class ConnectedReplica {
    private final OutputStream os;

    public ConnectedReplica(OutputStream os) {
        this.os = os;
    }

    public OutputStream getOs() {
        return os;
    }
}
