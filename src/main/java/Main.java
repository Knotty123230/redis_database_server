import redis.RedisSocket;
import redis.service.ApplicationInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        Map<String, String> parameters = extractArgs(args);
        System.out.println("args: " + Arrays.toString(args));
        int port = 6379;
        findRole(parameters);
        port = getPortFromApplicationParameters(parameters, port);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            Thread thread = new Thread(new RedisSocket(serverSocket));
            thread.start();
        } catch (IOException e) {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }

    private static void findRole(Map<String, String> parameters) {
        Map<String, String> applicationInfo = Main.applicationInfo.getInfo();
        String role = "master";
        if (parameters.containsKey("--replicaof")) {
            role = "slave";
        } else {
            applicationInfo.put("master_repl_offset", "0");
            applicationInfo.put("master_replid", "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb");
        }
        applicationInfo.put("role", role);
    }

    private static int getPortFromApplicationParameters(Map<String, String> parameters, int port) {
        if (parameters.containsKey("--port")) {
            port = Integer.parseInt(parameters.get("--port"));
        }

        return port;
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
