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
    private  RdbFile rdbFile;
    private static RdbFileInfo instance;
    private File file;
    private RdbFileInfo() {
    }

    // Static method to get the singleton instance
    public static synchronized RdbFileInfo getInstance() {
        if (instance == null) {
            instance = new RdbFileInfo();
        }
        return instance;
    }

    public byte[] getContent() {
        byte[] decode;
        try (Stream<String> stringStream = Files.lines(Path.of(file.getPath()))) {
            String rdbFile = stringStream.collect(Collectors.joining());
            decode = Base64.getDecoder().decode(rdbFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return decode;
    }
    public String getFileName(){
        return rdbFile.fileName();
    }
    public String getPath(){
        return rdbFile.path();
    }

    public void setFile(Map<String, String> parameters) {
        String path = "";
        String fileName = "";
        if (parameters.containsKey("--dir")){
             path = parameters.get("--dir");
        } else if (parameters.containsKey("--dbfilename")) {
             fileName = parameters.get("--dbfilename");
        }
        this.rdbFile = new RdbFile(path, fileName);
        this.file = new File( rdbFile.path() + "/" + rdbFile.fileName());
        if (file.exists()){
            file.mkdir();
            System.out.println(file.getPath());
            try (FileWriter writer = new FileWriter(file)){
                writer.write("UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==");
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
