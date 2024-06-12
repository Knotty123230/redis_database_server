package redis.service;

import redis.model.RdbFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RdbFileInfo {
    private RdbFile rdbFile;
    private static RdbFileInfo instance;

    private RdbFileInfo() {

    }

    public static synchronized RdbFileInfo getInstance() {
        if (instance == null) {
            instance = new RdbFileInfo();
        }
        return instance;
    }

    public byte[] getContent() {
        try (Stream<String> stringStream = Files.lines(Path.of(rdbFile.path() + "/" + rdbFile.fileName()))) {
            String readFile = stringStream.collect(Collectors.joining());
            return Base64.getDecoder().decode(readFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileName() {
        return rdbFile.fileName();
    }

    public String getPath() {
        return rdbFile.path();
    }

    public void setFile(Map<String, String> parameters) {
        String path = "";
        String fileName = "";
        System.out.println(parameters);
        if (parameters.containsKey("--dir")) {
            path = parameters.get("--dir");
        }
        if (parameters.containsKey("--dbfilename")) {
            fileName = parameters.get("--dbfilename");
        }
        if (path.isEmpty() || fileName.isEmpty()) {
            return;
        }
        this.rdbFile = new RdbFile(path, fileName);
        System.out.println(fileName);
        String pathname = rdbFile.path() + "/" + rdbFile.fileName();
        File file1 = new File(pathname);
        if (!file1.exists()) {
            boolean mkdir = file1.getParentFile().mkdirs();
            System.out.println(mkdir);
            try (FileWriter writer = new FileWriter(file1)) {
                writer.write("UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==");
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
