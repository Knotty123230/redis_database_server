package redis;

public enum Command {
    PING("ping"),
    PONG("pong");
    private final String value;
    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
