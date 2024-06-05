package redis;

import redis.model.Role;
import redis.service.ApplicationInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

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
                RedisClient task;
                task = new RedisClient(clientSocket);
                Thread thread = new Thread(task);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
