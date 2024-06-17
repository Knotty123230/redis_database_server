package redis.command.master;

import redis.command.CommandProcessor;
import redis.model.Command;
import redis.parser.CommandParser;
import redis.service.master.ReplicaReceiver;
import redis.service.master.ReplicaSender;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WaitCommandProcessor implements CommandProcessor {
    private final ReplicaSender replicaSender;
    private final CommandParser commandParser;
    private final ReplicaReceiver replicaReceiver;

    public WaitCommandProcessor(ReplicaSender replicaSender, ReplicaReceiver replicaReceiver) {
        this.replicaSender = replicaSender;
        this.commandParser = new CommandParser();
        this.replicaReceiver = replicaReceiver;
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        try {
            int numSlaves = Integer.parseInt(command.getFirst());
            long timeout = Long.parseLong(command.get(1));

            long startTime = System.currentTimeMillis();

            if (0 == replicaSender.getCountCommands()) {
                String response = ":" + replicaSender.getCountConnectedReplicas() + "\r\n";
                os.write(response.getBytes());
                return;
            }
            replicaSender.addCommand(commandParser.getResponseFromCommandArray(List.of(Command.REPLCONF.getValue(), "GETACK", "*")));
            while ((System.currentTimeMillis() - startTime) < timeout && replicaReceiver.getReceivedCount() < numSlaves) {
            }

            int receivedCount = replicaReceiver.getReceivedCount();
            String response = ":" + receivedCount + "\r\n";
            os.write(response.getBytes());
            os.flush();
            replicaReceiver.reset();
        } catch (NumberFormatException e) {
            String errorResponse = "-ERR invalid arguments\r\n";
            os.write(errorResponse.getBytes());
            os.flush();
        }
    }
}
