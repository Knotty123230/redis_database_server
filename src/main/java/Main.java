import redis.RedisClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Main {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        Map<String, String> parameters = extractArgs(args);
        System.out.println("Logs from your program will appear here!");
        int port = 6379;
        if (parameters.containsKey("--port")) {
            port = Integer.parseInt(parameters.get("--port"));
        }
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            serverSocket.setReuseAddress(true);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                RedisClient task = new RedisClient(clientSocket);
                Thread thread = new Thread(task);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static Map<String, String> extractArgs(String[] args) {
        Map<String, String> parameters = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (i + 1 >= args.length) break;
            parameters.put(args[i], args[i + 1]);
        }
        return parameters;
    }


}
