package redis.model;

public enum Role {
    MASTER("master"),
    SLAVE("slave");
    private final String value;

    Role(String slave) {
        value = slave;
    }

    public String getValue() {
        return value;
    }
}
