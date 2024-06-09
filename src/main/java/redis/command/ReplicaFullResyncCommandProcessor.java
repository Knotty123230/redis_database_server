package redis.command;

import java.io.OutputStream;
import java.util.List;

public class ReplicaFullResyncCommandProcessor implements CommandProcessor {
    @Override
    public void processCommand(List<String> command, OutputStream os) {
        System.out.println(command);
    }
}
