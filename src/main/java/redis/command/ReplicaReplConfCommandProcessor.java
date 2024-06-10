package redis.command;

import redis.command.model.Command;
import redis.parser.CommandParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplicaReplConfCommandProcessor implements CommandProcessor {
    private final CommandParser commandParser;

    public ReplicaReplConfCommandProcessor() {
        commandParser = new CommandParser();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) {
        System.out.println("ReplicaReplConfCommandProcessor processed command: " + command);
        try {
            os.write(commandParser.getResponseFromCommandArray(List.of(Command.REPLCONF.getValue(), "ACK", "0")).getBytes());
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
