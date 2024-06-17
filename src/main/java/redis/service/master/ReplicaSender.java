package redis.service.master;

import redis.model.Command;
import redis.model.ConnectedReplica;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ReplicaSender implements Closeable {
    private final Queue<ConnectedReplica> connectedReplicas;
    private final Queue<String> commands;
    private final ExecutorService executorService;
    private final AtomicInteger countCommands;

    private ReplicaSender() {
        this.connectedReplicas = new LinkedBlockingQueue<>();
        this.commands = new LinkedBlockingQueue<>();
        this.executorService = Executors.newCachedThreadPool();
        countCommands = new AtomicInteger(0);
    }

    public static ReplicaSender getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public String getCountConnectedReplicas() {
        return String.valueOf(connectedReplicas.size());
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
        if (command.toLowerCase().contains(Command.SET.getValue().toLowerCase()) || command.toLowerCase().contains("GETACK".toLowerCase())) {
            countCommands.getAndIncrement();
            System.out.println("MASTER added command to replica: " + command);
            commands.add(command);
        }
    }

    public int getCountCommands() {
        return countCommands.getAndSet(0);
    }

    public void addConnection(OutputStream outputStream) {
        connectedReplicas.add(new ConnectedReplica(outputStream));
        System.out.println("SIZE CONNECTED REPLICAS = " + connectedReplicas.size());
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    private static final class SingletonHelper {
        private static final ReplicaSender INSTANCE = new ReplicaSender();
    }
}
