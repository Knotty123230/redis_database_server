package redis.command.model;

public enum TypeCommand {
    STRING("STRING"), NONE("NONE");
    private final String value;
    TypeCommand(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
