import redis.RedisSocket;
import redis.handler.replica.ReplicaConnectionService;
import redis.service.ApplicationInfo;
import redis.service.ReplicaReceiver;
import redis.utils.Settings;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

public class Main {
    private static final ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

    public static void main(String[] args) {
        Map<String, String> parameters = Settings.extractArgs(args);
        int port = 6379;
        String[] masterPortAndHost = applicationInfo.findRole(parameters);
        port = Settings.extractPort(parameters, port);
        ReplicaConnectionService replicaConnectionService;
        try {
            replicaConnectionService = new ReplicaConnectionService(masterPortAndHost, port);
            replicaConnectionService.checkConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            Thread thread = new Thread(new RedisSocket(serverSocket, new ReplicaReceiver()));
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


}
