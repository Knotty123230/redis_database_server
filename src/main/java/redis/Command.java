package redis;

public enum Command {
    PING("PING"),
    PONG("PONG"),
    ECHO("ECHO");
    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
