package redis;

import redis.command.CommandProcessor;
import redis.factory.CommandFactory;

import java.io.*;
import java.net.Socket;

public class RedisClient implements Runnable {
    private final Socket socket;


    public RedisClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream();
        ) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                    boolean isValid = validateLine(line);
                    if (!isValid) continue;
                    byte[] bytes = processCommand(line);
                    outputStream.write(bytes);
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private boolean validateLine(String line) {
        return line.equalsIgnoreCase(Command.ECHO.getValue()) || line.equalsIgnoreCase(Command.PING.getValue());
    }

    private synchronized byte[] processCommand(String line) {
        CommandFactory commandFactory = new CommandFactory(line);
        CommandProcessor instance = commandFactory.getInstance();
        return instance.processCommand(line);
    }


}
