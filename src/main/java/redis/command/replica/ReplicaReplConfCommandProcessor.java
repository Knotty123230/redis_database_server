package redis.command.replica;

import redis.command.CommandProcessor;
import redis.model.Command;
import redis.model.CommandByteCounter;
import redis.parser.CommandParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplicaReplConfCommandProcessor implements CommandProcessor {
    private final CommandParser commandParser;
    private final CommandByteCounter commandByteCounter;

    public ReplicaReplConfCommandProcessor() {
        commandParser = new CommandParser();
        this.commandByteCounter = CommandByteCounter.getInstance();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) {
        System.out.println("ReplicaReplConfCommandProcessor processed command: " + command);
        try {
            byte[] bytes = commandParser.getResponseFromCommandArray(List.of(Command.REPLCONF.getValue(), "ACK", commandByteCounter.getBytes().toString())).getBytes();
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
