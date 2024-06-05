import redis.RedisSocket;
import redis.replicas.ReplicaConnectionService;
import redis.service.ApplicationInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

    public static void main(String[] args) {
        Map<String, String> parameters = extractArgs(args);
        int port = 6379;
        String[] masterPortAndHost = findRole(parameters);
        port = getPortFromApplicationParameters(parameters, port);
        checkConnection(masterPortAndHost, port);
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            Thread thread = new Thread(new RedisSocket(serverSocket));
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void checkConnection(String[] masterPortAndHost, int port) {
        if (masterPortAndHost.length == 0) {
            throw new RuntimeException("Master is not connected");
        }
        try {
            ReplicaConnectionService replicaConnectionService = new ReplicaConnectionService(masterPortAndHost, port);
            replicaConnectionService.getConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String[] findRole(Map<String, String> parameters) {
        Map<String, String> applicationInfo = Main.applicationInfo.getInfo();
        String[] masterPortAndHost = new String[]{};
        String role = "master";
        if (parameters.containsKey("--replicaof")) {
            masterPortAndHost = parameters.get("--replicaof").split(" ");
            role = "slave";

        } else {
            applicationInfo.put("master_repl_offset", "0");
            applicationInfo.put("master_replid", "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb");
        }
        applicationInfo.put("role", role);
        return masterPortAndHost;
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
