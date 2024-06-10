package redis.command.master;

import redis.command.model.Command;
import redis.parser.CommandParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplconfCommandSender extends CommandSender {
    private final CommandParser commandParser;

    public ReplconfCommandSender() {
        commandParser = new CommandParser();
    }

    @Override
    public void send(OutputStream os) throws IOException {
        os.write(commandParser.getResponseFromCommandArray(List.of(Command.REPLCONF.getValue(), "GETACK", "*")).getBytes());
        os.flush();
    }
}
