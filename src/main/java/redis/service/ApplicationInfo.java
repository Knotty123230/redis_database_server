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

    public Map<String, String> getInfo() {
        return this.info;
    }
}
