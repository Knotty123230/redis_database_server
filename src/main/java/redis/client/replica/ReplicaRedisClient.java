package redis.client.replica;

import redis.client.Client;
import redis.command.CommandHandler;
import redis.handler.replica.ReplicaCommandHandler;
import redis.model.CommandByteCounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ReplicaRedisClient extends Client {
    private final CommandByteCounter commandByteCounter;

    public ReplicaRedisClient(BufferedReader reader, Socket socket) {
        super(reader, socket);
        this.commandByteCounter = CommandByteCounter.getInstance();
    }


    @Override
    protected void handleClient(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) continue;
            List<String> parsedCommands = commandParser.parseCommand(bufferedReader, line);
            System.out.println("ReplicaRedisClient parse: " + parsedCommands);
            addBytes(parsedCommands);
            CommandHandler commandHandler = new ReplicaCommandHandler(parsedCommands, outputStream);
            Thread thread = new Thread(commandHandler);
            thread.start();
        }
    }

    private void addBytes(List<String> parsedCommands) {
        if (commandByteCounter.isFirst()) {
            commandByteCounter.setIsFirst(false);
        } else {
            commandByteCounter.addBytes(commandParser.getResponseFromCommandArray(parsedCommands).getBytes().length);
        }
    }
}
