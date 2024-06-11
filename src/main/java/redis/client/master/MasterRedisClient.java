package redis.client.master;

import redis.client.Client;
import redis.handler.master.MasterCommandHandler;
import redis.service.ReplicaSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class MasterRedisClient extends Client {

    public MasterRedisClient(Socket socket) {
        super(socket, new MasterCommandHandler(ReplicaSender.getInstance()));
    }

    @Override
    protected void handleClient(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) continue;
            List<String> parsedCommands = commandParser.parseCommand(bufferedReader, line);
            commandHandler.processCommand(parsedCommands, outputStream);
        }
    }
}
