package redis;

public enum Command {
    PING("PING"),
    PONG("PONG"),
    ECHO("ECHO"),
    SET("SET"), GET("GET");
    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
