package redis.command;

import redis.command.model.Command;
import redis.model.ConnectedReplica;
import redis.service.ApplicationInfo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FullResyncCommandProcessor implements CommandProcessor {
    private final ApplicationInfo applicationInfo;
    private final Socket socket;

    public FullResyncCommandProcessor(Socket socket) {
        this.applicationInfo = ApplicationInfo.getInstance();
        this.socket = socket;
    }

    @Override
    public  void processCommand(List<String> command, OutputStream os) {
        ConnectedReplica connectedReplica = new ConnectedReplica(socket.getPort(), socket.getInetAddress().getHostAddress());
        synchronized (this){
            byte[] decode;
            File file = new File("rdb.rdb");
            try (Stream<String> stringStream = Files.lines(Path.of(file.getPath()))) {
                String rdbFile = stringStream.collect(Collectors.joining());
                decode = Base64.getDecoder().decode(rdbFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                os.write(("+" + Command.FULLRESYNC.getValue() + " " + applicationInfo.getInfo().get("master_replid") + " 0\r\n").getBytes());
                os.flush();
                os.write(("$" + decode.length + "\r\n").getBytes());
                os.write(decode);
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
