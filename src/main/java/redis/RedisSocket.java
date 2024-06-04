package redis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisSocket extends Thread {
    private final ServerSocket serverSocket;
    private final int masterPort;

    public RedisSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.masterPort = -1;
    }

    public RedisSocket(ServerSocket serverSocket, int masterPort) {
        this.serverSocket = serverSocket;
        this.masterPort = masterPort;
    }

    @Override
    public void run() {
        try {
            serverSocket.setReuseAddress(true);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                RedisClient task;
                if (masterPort != -1) {
                    task = new RedisClient(clientSocket);
                } else {
                    task = new RedisClient(clientSocket);
                }
                Thread thread = new Thread(task);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
