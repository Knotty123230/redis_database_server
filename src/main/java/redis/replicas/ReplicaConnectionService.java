package redis.replicas;

import redis.command.model.Command;
import redis.parser.CommandParser;
import redis.replicas.handler.ConnectionHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

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
            System.out.println("ReplicaConnectionService: CONNECTION SUCCESS");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
