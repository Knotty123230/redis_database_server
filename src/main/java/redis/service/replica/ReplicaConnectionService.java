package redis.service.replica;

import redis.client.Client;
import redis.client.replica.ReplicaRedisClient;
import redis.handler.replica.ConnectionHandler;
import redis.parser.CommandParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ReplicaConnectionService {
    private final CommandParser commandParser;
    private final int port;
    private Socket socket;

    public ReplicaConnectionService(String[] masterPortAndHost, int port) throws IOException {
        this.port = port;
        this.commandParser = new CommandParser();
        if (masterPortAndHost.length > 0) {
            this.socket = new Socket(masterPortAndHost[0], Integer.parseInt(masterPortAndHost[1]));
        }
    }

    public void getConnection() {
        ConnectionHandler connectionHandler = new ConnectionHandler(socket, commandParser, port);
        BufferedReader bufferedReader = connectionHandler.handleConnection();
        Client redisClient = new ReplicaRedisClient(bufferedReader, socket);
        Thread thread = new Thread(redisClient);
        thread.start();
        System.out.println("ReplicaConnectionService: CONNECTION SUCCESS");
    }

    public void checkConnection() {
        if (this.socket != null) {
            getConnection();
        }
    }
}
