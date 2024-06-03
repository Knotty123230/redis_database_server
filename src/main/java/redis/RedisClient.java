package redis;

import redis.command.CommandExtractor;
import redis.command.CommandProcessor;
import redis.factory.CommandFactory;

import java.io.*;
import java.net.Socket;

public class RedisClient implements Runnable {
    private final Socket socket;
    private final CommandExtractor commandExtractor = new CommandExtractor();

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
            byte[] response = processLine(bufferedReader, line);
            if (response.length > 0) {
                outputStream.write(response);
                outputStream.flush();
            }
        }
    }

    private byte[] processLine(BufferedReader bufferedReader, String line) throws IOException {
        switch (line.toUpperCase()) {
            case "ECHO":
                return processCommand(Command.ECHO, commandExtractor.getCommandMessage(bufferedReader));
            case "PING":
                return processCommand(Command.PING, line);
            case "SET":
                return processCommand(Command.SET, commandExtractor.getCommandMessage(bufferedReader));
            case "GET":
                return processCommand(Command.GET, commandExtractor.getCommandMessage(bufferedReader) + " " + commandExtractor.getCommandMessage(bufferedReader));

            default:
                return new byte[0];
        }
    }

    private synchronized byte[] processCommand(Command command, String commandArgument) {
        CommandFactory commandFactory = new CommandFactory(command);
        CommandProcessor commandProcessor = commandFactory.getInstance();
        return commandProcessor.processCommand(commandArgument);
    }
}
