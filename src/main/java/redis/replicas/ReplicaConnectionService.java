package redis.replicas;

import redis.command.model.Command;
import redis.parser.CommandParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ReplicaConnectionService {
    private final Socket socket;
    private final CommandParser commandParser;
    private final int port;

    public ReplicaConnectionService(String[] masterPortAndHost, int port) throws IOException {
        this.port = port;
        this.socket = new Socket(masterPortAndHost[0], Integer.parseInt(masterPortAndHost[1]));
        this.commandParser = new CommandParser();
    }

    public void getConnection() {
        try (OutputStream outputStream = socket.getOutputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            byte[] bytes = commandParser.getResponseFromCommandArray(List.of(Command.PING.getValue().toLowerCase())).getBytes();
            outputStream.write(bytes);
            outputStream.flush();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if (line.isEmpty()) continue;
                System.out.println("GET CONNECTION : process line: " + line);
                if (line.equalsIgnoreCase("+" + Command.PONG.getValue())){
                    sendReplConfCommandToMaster(bufferedReader, outputStream);
                }
            }
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private void sendReplConfCommandToMaster(BufferedReader bufferedReader, OutputStream outputStream) {
        try {
            System.out.println("LOCAL PORT: " + port);
            outputStream.write(commandParser.getResponseFromCommandArray(List.of(Command.REPLCONF.getValue(), "listening-port", String.valueOf(port))).getBytes());
            outputStream.flush();
            String line = bufferedReader.readLine();
            if (!line.isEmpty()){
                outputStream.write(commandParser.getResponseFromCommandArray(List.of(Command.REPLCONF.getValue(), "capa", "npsync2")).getBytes());
            }
            String respCapa = bufferedReader.readLine();
            if (!respCapa.isEmpty()){
                outputStream.write(commandParser.getResponseFromCommandArray(List.of(Command.PSYNC.getValue(),"?", "-1")).getBytes());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
