package redis;

import redis.client.master.MasterRedisClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisSocket extends Thread {
    private final ServerSocket serverSocket;

    public RedisSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }


    @Override
    public void run() {
        try {
            serverSocket.setReuseAddress(true);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                MasterRedisClient task;
                task = new MasterRedisClient(clientSocket);
                Thread thread = new Thread(task);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
