package redis.utils;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    public static int extractPort(Map<String, String> parameters, int port) {
        if (parameters.containsKey("--port")) {
            port = Integer.parseInt(parameters.get("--port"));
        }
        return port;
    }

    public static Map<String, String> extractArgs(String[] args) {
        Map<String, String> parameters = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (i + 1 >= args.length) break;
            parameters.put(args[i], args[i + 1]);
            System.out.println(parameters);
        }
        return parameters;
    }
}
