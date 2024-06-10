package redis.replicas.handler;

import redis.command.CommandHandler;
import redis.command.model.Command;
import redis.parser.CommandParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler {
    private final Socket socket;
    private final CommandParser commandParser;
    private final int port;

    public ConnectionHandler(Socket socket, CommandParser commandParser, int port) {
        this.socket = socket;
        this.commandParser = commandParser;
        this.port = port;
    }

    public BufferedReader handleConnection() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            byte[] bytes = commandParser.getResponseFromCommandArray(List.of(Command.PING.getValue().toLowerCase())).getBytes();
            outputStream.write(bytes);
            outputStream.flush();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) continue;
                System.out.println("GET CONNECTION : process line: " + line);
                if (line.equalsIgnoreCase("+" + Command.PONG.getValue())) {
                    sendReplConfCommandToMaster(bufferedReader, outputStream);
                } else if (line.startsWith("+FULLRESYNC")) {
                    String s = bufferedReader.readLine();
                    String substringed = s.substring(1);
                    int charsToSkip = Integer.parseInt(substringed) - 1;
                    long skip = bufferedReader.skip(charsToSkip);
                    System.out.println("skipped bytes : " + skip);
                    return bufferedReader;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void sendReplConfCommandToMaster(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        System.out.println("LOCAL PORT: " + port);
        String replconfResp = "";
        replconfResp = getReplconfResp(bufferedReader, outputStream);

        if (!replconfResp.isEmpty()) {
            replconfResp = sendReplConfCapa(bufferedReader, outputStream);
        }
        if (!replconfResp.isEmpty()) {
            outputStream.write(commandParser.getResponseFromCommandArray(List.of(Command.PSYNC.getValue(), "?", "-1")).getBytes());
        }
    }

    private String sendReplConfCapa(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        outputStream.write(commandParser.getResponseFromCommandArray(List.of(
                        Command.REPLCONF.getValue(),
                        "capa",
                        "npsync2")
                ).getBytes()
        );
        outputStream.flush();
        return bufferedReader.readLine();
    }

    private String getReplconfResp(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        outputStream.write(commandParser.getResponseFromCommandArray(List.of(
                        Command.REPLCONF.getValue(),
                        "listening-port",
                        String.valueOf(port))
                ).getBytes()
        );
        outputStream.flush();
        return bufferedReader.readLine();
    }
}
