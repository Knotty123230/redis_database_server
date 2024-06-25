package redis.model;

public enum Command {
    ACK("ACK"),
    CONFIG("CONFIG"),
    ECHO("ECHO"),
    FULLRESYNC("FULLRESYNC"),
    GET("GET"),
    INCR("INCR"),
    INFO("INFO"),
    KEYS("KEYS"),
    PING("PING"),
    PONG("PONG"),
    PSYNC("PSYNC"),
    PX("PX"),
    REPLCONF("REPLCONF"),
    REPLICATION("REPLICATION"),
    SET("SET"),
    TYPE("TYPE"),
    WAIT("WAIT"),
    XADD("XADD"),
    XRANGE("XRANGE"),
    XREAD("XREAD"), MULTI("MULTI"), EXEC("EXEC");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
