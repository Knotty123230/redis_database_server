package redis.handler.replica;

import redis.command.sender.CommandSender;
import redis.command.sender.PingCommandSender;
import redis.command.sender.ReplConfCommandSender;
import redis.model.Command;
import redis.parser.CommandParser;
import redis.service.RdbBytesReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler {
    private final Socket socket;
    private final RdbBytesReader reader;
    private final int port;
    private final CommandParser commandParser;
    private CommandSender commandSender;

    public ConnectionHandler(Socket socket, CommandParser commandParser, int port) {
        this.socket = socket;
        commandSender = new PingCommandSender(commandParser);
        this.port = port;
        this.reader = new RdbBytesReader();
        this.commandParser = commandParser;
    }

    public BufferedReader handleConnection() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            commandSender.sendCommand(bufferedReader, outputStream);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) continue;
                System.out.println("GET CONNECTION : process line: " + line);
                if (line.equalsIgnoreCase("+" + Command.PONG.getValue())) {
                    this.commandSender = new ReplConfCommandSender(String.valueOf(port), commandParser);
                    commandSender.sendCommand(bufferedReader, outputStream);
                } else if (line.startsWith("+FULLRESYNC")) {
                    return reader.read(bufferedReader);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
