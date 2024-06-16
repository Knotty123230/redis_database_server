package redis.command.model;

public enum Command {
    ACK("ACK"),
    CONFIG("CONFIG"),
    ECHO("ECHO"),
    FULLRESYNC("FULLRESYNC"),
    GET("GET"),
    INFO("INFO"),
    KEYS("KEYS"),
    PING("PING"),
    PONG("PONG"),
    PSYNC("PSYNC"),
    PX("PX"),
    REPLCONF("REPLCONF"),
    REPLICATION("REPLICATION"),
    SET("SET"),
    WAIT("WAIT"),
    TYPE("TYPE"), XADD("XADD"), XRANGE("XRANGE"), XREAD("XREAD");
    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
