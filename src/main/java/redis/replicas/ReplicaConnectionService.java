package redis.replicas;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ReplicaConnectionService {
    private final Socket socket;

    public ReplicaConnectionService(String[] masterPortAndHost) throws IOException {
        this.socket = new Socket(masterPortAndHost[0], Integer.parseInt(masterPortAndHost[1]));
    }

    public void getConnection() {
        try (OutputStream outputStream = socket.getOutputStream()) {
            outputStream.write("*1\r\n$4\r\nping\r\n".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

    }
}
