package redis.command;

public class EchoCommandProcessor implements CommandProcessor {
    @Override
    public byte[] processCommand(String command) {
        return ("$" + command.length() + "\r\n" + command + "\r\n").getBytes();
    }
}
