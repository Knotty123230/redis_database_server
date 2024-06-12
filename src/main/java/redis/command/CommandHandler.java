package redis.command;

public interface CommandHandler extends Runnable {
    boolean processCommand();
}
