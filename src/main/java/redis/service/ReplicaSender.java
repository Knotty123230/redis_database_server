package redis.service;

import redis.model.ConnectedReplica;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReplicaSender extends Thread{
    private final List<ConnectedReplica> connectedReplicas;
    public ReplicaSender(){
        this.connectedReplicas = new CopyOnWriteArrayList<>();
    }
    public void sendToReplicas(String command) throws Exception {
        for (ConnectedReplica connectedReplica : connectedReplicas) {
            try (Socket socket = new Socket(connectedReplica.getReplicaHost(), connectedReplica.getReplicaPort())) {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(command.getBytes());
            }

        }

    }

    @Override
    public void run() {
        super.run();
    }

    public List<ConnectedReplica> getConnectedReplicas() {
        return connectedReplicas;
    }
}
