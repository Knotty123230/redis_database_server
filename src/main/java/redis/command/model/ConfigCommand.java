package redis.command.model;

public enum ConfigCommand {
    DIR("DIR"),
    DBFILENAME("DBFILENAME");
    private final String value;

    ConfigCommand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
