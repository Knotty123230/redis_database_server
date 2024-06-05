package redis.command.model;

public enum Command {
    PING("PING"),
    PONG("PONG"),
    ECHO("ECHO"),
    SET("SET"), GET("GET"), PX("PX"), REPLICATION("REPLICATION"), INFO("INFO"), REPLCONF("REPLCONF"), PSYNC("PSYNC"), FULLRESYNC("FULLRESYNC");
    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
