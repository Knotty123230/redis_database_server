package redis.replicas;

import redis.RedisClient;
import redis.RedisSocket;
import redis.parser.CommandParser;
import redis.replicas.handler.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReplicaConnectionService {
    private final Socket socket;
    private final CommandParser commandParser;
    private final int port;

    public ReplicaConnectionService(String[] masterPortAndHost, int port) throws IOException {
        this.port = port;
        this.socket = new Socket(masterPortAndHost[0], Integer.parseInt(masterPortAndHost[1]));
        this.commandParser = new CommandParser();
    }

    public void getConnection() {
        try {

            ConnectionHandler connectionHandler = new ConnectionHandler(socket, commandParser, port);
            connectionHandler.handleConnection();
            RedisClient redisClient = new RedisClient(socket);
            Thread thread = new Thread(redisClient);
            thread.start();
            System.out.println("ReplicaConnectionService: CONNECTION SUCCESS");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
