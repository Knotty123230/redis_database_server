package redis.model;

public class ConnectedReplica {
    private final Integer replicaPort;
    private final String replicaHost;

    public ConnectedReplica(Integer replicaPort, String replicaHost) {
        this.replicaPort = replicaPort;
        this.replicaHost = replicaHost;
    }

    public Integer getReplicaPort() {
        return replicaPort;
    }

    public String getReplicaHost() {
        return replicaHost;
    }
}
