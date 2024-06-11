package redis.command.sender;

import redis.parser.CommandParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public abstract class CommandSender {
    protected final CommandParser commandParser;

    protected CommandSender(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    public abstract void sendCommand(BufferedReader bufferedReader, OutputStream outputStream) throws IOException;
}
