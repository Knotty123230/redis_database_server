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

    public static synchronized RdbFileInfo getInstance() {
        if (instance == null) {
            instance = new RdbFileInfo();
        }
        return instance;
    }

    public byte[] getContent() {
        System.out.println(this.file.getPath());
        try (Stream<String> stringStream = Files.lines(Path.of(this.file.getPath()))) {
            String readFile = stringStream.collect(Collectors.joining());
            return Base64.getDecoder().decode(readFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        System.out.println(parameters);
        if (parameters.containsKey("--dir")){
             path = parameters.get("--dir");
        }
        if (parameters.containsKey("--dbfilename")) {
             fileName = parameters.get("--dbfilename");
        }
        if (path.isEmpty() || fileName.isEmpty() ){
            return;
        }
        this.rdbFile = new RdbFile(path, fileName);
        System.out.println(fileName);
        this.file = new File( rdbFile.path() + "/" + rdbFile.fileName());
        if (file.exists()){
            file.mkdirs();
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
