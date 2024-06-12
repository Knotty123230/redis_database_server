package redis.model;

import java.io.File;

public class RdbFile {
    private final String path;
    private final String fileName;

    public RdbFile(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }
}
