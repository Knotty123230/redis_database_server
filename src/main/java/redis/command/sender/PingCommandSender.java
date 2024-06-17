package redis.command.sender;

import redis.model.Command;
import redis.parser.CommandParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class PingCommandSender extends CommandSender {
    public PingCommandSender(CommandParser commandParser) {
        super(commandParser);
    }

    @Override
    public void sendCommand(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        byte[] bytes = commandParser.getResponseFromCommandArray(List.of(Command.PING.getValue().toLowerCase())).getBytes();
        outputStream.write(bytes);
        outputStream.flush();

    }
}
