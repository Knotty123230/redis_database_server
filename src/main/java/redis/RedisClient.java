package redis;

import redis.command.CommandProcessor;
import redis.command.model.Command;
import redis.factory.CommandFactory;
import redis.model.Role;
import redis.parser.CommandParser;
import redis.service.ApplicationInfo;
import redis.utils.CommandUtil;
import redis.utils.ResponseUtil;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class RedisClient implements Runnable {
    private final Socket socket;
    private final CommandParser commandParser = new CommandParser();

    public RedisClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            System.out.println("HANDLE CLIENT PORT: " + socket.getPort());
            handleClient(bufferedReader, outputStream);
        } catch (IOException e) {
            System.out.println("IOException while handling client: " + e.getMessage());
        }
    }

    private void handleClient(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println("FETCHING LINE CLIENT: " + line);
            if (line.isEmpty()) continue;
            List<String> list = commandParser.parseCommand(bufferedReader, line);
            System.out.println("PARSED COMMANDS : " + list);
            processCommand(list, outputStream);
        }
    }


    private synchronized void processCommand(List<String> commands, OutputStream os) {
        String remove = commands.removeFirst();
        Command command = CommandUtil.getCommand(remove);
        System.out.println("RedisClient processCommand: " + Objects.requireNonNull(command).getValue());
        CommandFactory commandFactory = new CommandFactory(command);
        CommandProcessor commandProcessor = commandFactory.getInstance();
         commandProcessor.processCommand(commands, os);
    }


}
