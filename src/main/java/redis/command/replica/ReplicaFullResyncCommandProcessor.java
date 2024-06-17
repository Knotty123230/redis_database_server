package redis.command.replica;

import redis.command.CommandProcessor;
import redis.model.Command;
import redis.parser.CommandParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplicaFullResyncCommandProcessor implements CommandProcessor {
    private final CommandParser commandParser;

    public ReplicaFullResyncCommandProcessor() {
        commandParser = new CommandParser();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        System.out.println(command);
        os.write(commandParser.getResponseFromCommandArray(List.of(Command.REPLCONF.getValue(), "ACK", "0")).getBytes());
        os.flush();
    }
}
