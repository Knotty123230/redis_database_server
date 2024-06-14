package redis.command.master;

import redis.command.CommandProcessor;
import redis.command.model.Command;
import redis.service.master.ReplicaReceiver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplConfCommandProcessor implements CommandProcessor {

    private final ReplicaReceiver replicaReceiver;


    public ReplConfCommandProcessor(ReplicaReceiver replicaReceiver) {
        this.replicaReceiver = replicaReceiver;
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) {
        try {
            if (command.getFirst().equalsIgnoreCase(Command.ACK.getValue())) {
                System.out.println("PROCESS REPLCONF COMMAND : " + command);
                replicaReceiver.receive();
                return;
            }

            os.write("+OK\r\n".getBytes());
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
