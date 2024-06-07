package redis.service;

import redis.command.model.Command;
import redis.model.ConnectedReplica;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReplicaSender implements Runnable {
    private static ReplicaSender replicaSender;
    private final Queue<ConnectedReplica> connectedReplicas;
    private final Queue<String> commands;

    private ReplicaSender() {
        this.connectedReplicas = new LinkedBlockingQueue<>();
        this.commands = new ConcurrentLinkedQueue<>();
    }

    public static synchronized ReplicaSender getInstance() {
        if (replicaSender == null) {
            replicaSender = new ReplicaSender();
        }
        return replicaSender;
    }


    @Override
    public void run() {
        while (true) {
            if (!commands.isEmpty()) {
                String poll = commands.poll();
                for (ConnectedReplica connectedReplica : connectedReplicas) {
                    OutputStream outputStream = connectedReplica.getOs();
                    try {
                        outputStream.write(poll.getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void addCommand(String command) {
        if (command.toLowerCase().contains(Command.SET.getValue().toLowerCase())) {
            commands.add(command);
        }
    }

    public void addConnection(OutputStream outputStream) {
        ConnectedReplica connectedReplica = new ConnectedReplica(outputStream);
        connectedReplicas.add(connectedReplica);
    }


}
