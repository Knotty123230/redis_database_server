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

    public static synchronized ReplicaSender getInstance() {
        if (replicaSender == null) {
            replicaSender = new ReplicaSender();
        }
        return replicaSender;
    }


    @Override
    public void run() {
        while (true) {
            if (!commands.isEmpty() && !connectedReplicas.isEmpty()) {
                for (String command : commands) {
                    for (ConnectedReplica connectedReplica : connectedReplicas) {
                        if (connectedReplica != null) {
                            writeCommandToReplica(command, connectedReplica);
                        }
                    }
                }
                commands.clear();
            }
        }
    }

    private void writeCommandToReplica(String command, ConnectedReplica connectedReplica) {
        OutputStream os = connectedReplica.getOs();
        if (command.toLowerCase().contains(Command.SET.getValue().toLowerCase())) {
            try {
                os.write(command.getBytes());
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Queue<String> getCommands() {
        return commands;
    }
    public synchronized void addCommand(String command){
        commands.add(command);
    }
    public synchronized void addConnectedReplica(OutputStream os){
        ConnectedReplica connectedReplica = new ConnectedReplica(os);
        connectedReplicas.add(connectedReplica);
    }

    public List<ConnectedReplica> getConnectedReplicas() {
        return connectedReplicas;
    }
}
