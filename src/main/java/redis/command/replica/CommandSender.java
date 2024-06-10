package redis.command.replica;

import java.io.IOException;
import java.io.OutputStream;

public abstract class CommandSender {
    public abstract void send(OutputStream os) throws IOException;
}
