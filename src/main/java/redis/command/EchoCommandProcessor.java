package redis.command;

public class EchoCommandProcessor implements CommandProcessor {
    @Override
    public byte[] processCommand(String command) {
        System.out.printf("Processing ECHO command %s%n", command);
        return ResponseUtil.getResponse(command);
    }
}
