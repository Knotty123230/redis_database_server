package redis.socket;

import redis.client.Client;
import redis.client.master.MasterRedisClient;
import redis.service.master.ReplicaReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisSocket extends Thread {
    private final ServerSocket serverSocket;
    private final ReplicaReceiver replicaReceiver;


    public RedisSocket(ServerSocket serverSocket, ReplicaReceiver replicaReceiver) {
        this.serverSocket = serverSocket;
        this.replicaReceiver = replicaReceiver;
    }


    @Override
    public void run() {
        try {
            serverSocket.setReuseAddress(true);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                Client task = new MasterRedisClient(clientSocket, replicaReceiver);
                Thread thread = new Thread(task);
                thread.start();
            }


        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
