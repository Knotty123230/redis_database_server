package redis.model;

import java.io.OutputStream;

public record ConnectedReplica(OutputStream os) {
}
