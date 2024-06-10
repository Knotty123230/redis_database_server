package redis.command.replica;

import redis.command.CommandProcessor;

import java.io.OutputStream;
import java.util.List;

public class ReplicaEchoCommandProcessor implements CommandProcessor {
    @Override
    public void processCommand(List<String> command, OutputStream os) {

    }
}
