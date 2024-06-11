package redis.service;

import redis.command.model.Command;
import redis.model.ConnectedReplica;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ReplicaSender implements Closeable {
    private final Queue<ConnectedReplica> connectedReplicas;
    private final List<ConnectedReplica> connectedReplicasUnmodifiedList;
    private final Queue<String> commands;
    private final ExecutorService executorService;

    private ReplicaSender() {
        this.connectedReplicas = new LinkedBlockingQueue<>();
        this.commands = new LinkedBlockingQueue<>();
        this.executorService = Executors.newCachedThreadPool();
        connectedReplicasUnmodifiedList = new CopyOnWriteArrayList<>();
    }

    public static ReplicaSender getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public String getCountConnectedReplicas() {
        return String.valueOf(connectedReplicasUnmodifiedList.size());
    }

    private static class SingletonHelper {
        private static final ReplicaSender INSTANCE = new ReplicaSender();
    }

    public void start() {
        executorService.submit(this::processCommands);
    }

    private void processCommands() {
        while (true) {
            String command = commands.poll();
            if (command != null) {
                for (ConnectedReplica replica : connectedReplicas) {
                    OutputStream outputStream = replica.os();
                    try {
                        outputStream.write(command.getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void addCommand(String command) {
        System.out.println("MASTER added command to replica: " + command);
        if (command.toLowerCase().contains(Command.SET.getValue().toLowerCase())) {
            commands.add(command);
        }
    }

    public void addConnection(OutputStream outputStream) {
        connectedReplicasUnmodifiedList.add(new ConnectedReplica(outputStream));
        connectedReplicas.add(new ConnectedReplica(outputStream));
    }

    @Override
    public void close() throws IOException {
        executorService.shutdown();
    }
}
