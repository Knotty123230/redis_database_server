package redis;

import java.net.Socket;

public class RedisClientConnection extends Thread{
    private final Socket socket;

    public RedisClientConnection(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void start() {
        new RedisClient(socket).run();
    }
}
