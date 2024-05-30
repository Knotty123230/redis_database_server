import redis.RedisClient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        Socket clientSocket = null;
        int port = 6379;
        try (ServerSocket serverSocket = new ServerSocket(port);){

            serverSocket.setReuseAddress(true);
            while (!serverSocket.isClosed()) {
                clientSocket = serverSocket.accept();
                RedisClient task = new RedisClient(clientSocket);
                new Thread(task).start();
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
