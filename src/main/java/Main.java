import redis.RedisClient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket;
        Socket clientSocket = null;
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (true) {
                clientSocket = serverSocket.accept();
                RedisClient redisClient = new RedisClient(clientSocket);
                new Thread(redisClient).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }  finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }


}
