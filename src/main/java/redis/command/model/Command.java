package redis.command.model;

public enum Command {
    ECHO("ECHO"),
    FULLRESYNC("FULLRESYNC"),
    GET("GET"),
    INFO("INFO"),
    PING("PING"),
    PONG("PONG"),
    PSYNC("PSYNC"),
    PX("PX"),
    REPLCONF("REPLCONF"),
    REPLICATION("REPLICATION"),
    SET("SET");
    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
