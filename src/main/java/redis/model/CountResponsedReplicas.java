package redis.model;

public class CountResponsedReplicas {
    private final Integer count;

    public CountResponsedReplicas(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }
}
