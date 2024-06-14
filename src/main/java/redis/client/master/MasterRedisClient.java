package redis.client.master;

import redis.client.Client;
import redis.command.CommandHandler;
import redis.handler.master.MasterCommandHandler;
import redis.service.master.ReplicaReceiver;
import redis.service.master.ReplicaSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class MasterRedisClient extends Client {

    private final ReplicaReceiver replicaReceiver;

    public MasterRedisClient(Socket socket, ReplicaReceiver replicaReceiver) {
        super(socket);
        this.replicaReceiver = replicaReceiver;
    }

    @Override
    protected void handleClient(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) continue;
            List<String> parsedCommands = commandParser.parseCommand(bufferedReader, line);
            CommandHandler commandHandler = new MasterCommandHandler(replicaReceiver, ReplicaSender.getInstance(), parsedCommands, outputStream);
            Thread thread = new Thread(commandHandler);
            thread.start();
        }
    }
}
