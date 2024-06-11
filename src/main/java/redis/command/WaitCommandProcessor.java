package redis.command;

import redis.service.ReplicaSender;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WaitCommandProcessor implements CommandProcessor {
    private final ReplicaSender replicaSender;

    public WaitCommandProcessor() {
        replicaSender = ReplicaSender.getInstance();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {




        String response = ":" + replicaSender.getCountConnectedReplicas() + "\r\n";
        os.write(response.getBytes());
        os.flush();
    }
}
