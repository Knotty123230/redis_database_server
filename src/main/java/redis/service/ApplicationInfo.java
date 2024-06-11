package redis.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationInfo {
    private static ApplicationInfo applicationInfo;
    private final Map<String, String> info = new ConcurrentHashMap<>();

    private ApplicationInfo() {
        info.put("role", "master");
    }

    public static ApplicationInfo getInstance() {
        if (applicationInfo == null) {
            applicationInfo = new ApplicationInfo();
        }
        return applicationInfo;
    }
    public String[] findRole(Map<String, String> parameters) {
        String[] masterPortAndHost = new String[]{};
        String role = "master";
        if (parameters.containsKey("--replicaof")) {
            masterPortAndHost = parameters.get("--replicaof").split(" ");
            role = "slave";

        } else {
            info.put("master_repl_offset", "0");
            info.put("master_replid", "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb");
        }
        info.put("role", role);
        return masterPortAndHost;
    }

    public Map<String, String> getInfo() {
        return this.info;
    }
}
