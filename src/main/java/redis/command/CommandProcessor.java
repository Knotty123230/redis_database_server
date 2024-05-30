package redis.command;

public interface CommandProcessor {
    byte[] processCommand(String command);
}
