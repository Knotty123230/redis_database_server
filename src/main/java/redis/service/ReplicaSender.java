package redis.service;

import redis.command.model.Command;
import redis.model.ConnectedReplica;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReplicaSender implements Runnable {
    private static ReplicaSender replicaSender;
    private final List<ConnectedReplica> connectedReplicas;
    private final Queue<String> commands = new ConcurrentLinkedQueue<>();

    private ReplicaSender() {
        this.connectedReplicas = new CopyOnWriteArrayList<>();
    }

    public static ReplicaSender getInstance() {
        if (replicaSender == null) {
            replicaSender = new ReplicaSender();
        }
        return replicaSender;
    }


    @Override
    public void run() {
        while (true) {
            if (!commands.isEmpty() && !connectedReplicas.isEmpty()) {
                for (ConnectedReplica connectedReplica : connectedReplicas) {
                    OutputStream os = connectedReplica.getOs();
                    for (int i = 0; i < commands.size(); i++) {
                        String command = commands.poll();
                        if (command.toLowerCase().contains(Command.SET.getValue().toLowerCase())) {
                            try {
                                os.write(command.getBytes());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

            }

        }
    }


    public Queue<String> getCommands() {
        return commands;
    }

    public List<ConnectedReplica> getConnectedReplicas() {
        return connectedReplicas;
    }
}
