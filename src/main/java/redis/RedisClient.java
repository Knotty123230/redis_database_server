package redis;

import redis.command.CommandParser;
import redis.command.CommandProcessor;
import redis.command.CommandUtil;
import redis.factory.CommandFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;

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
            handleClient(bufferedReader, outputStream);
        } catch (IOException e) {
            System.out.println("IOException while handling client: " + e.getMessage());
        }
    }

    private void handleClient(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) continue;
            List<String> list = commandParser.parseCommand(bufferedReader, line);
            byte[] response = processCommand(list);
            if (response.length > 0) {
                outputStream.write(response);
                outputStream.flush();
            }
        }
    }


    private synchronized byte[] processCommand(List<String> commands) {
        String remove = commands.removeFirst();
        Command command = CommandUtil.getCommand(remove);
        CommandFactory commandFactory = new CommandFactory(command);
        CommandProcessor commandProcessor = commandFactory.getInstance();
        return commandProcessor.processCommand(commands);
    }


}
