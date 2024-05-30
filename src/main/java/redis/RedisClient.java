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
                        byte[] response = validateLine(bufferedReader, line);
                        if (response.length == 0) continue;
                        outputStream.write(response);
                        outputStream.flush();

                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private byte[] validateLine(BufferedReader bufferedReader, String line) throws IOException {
        if (line.equalsIgnoreCase(Command.ECHO.getValue())){
            bufferedReader.readLine();
            String command = bufferedReader.readLine();
            return processCommand(command);
        }else if (line.equalsIgnoreCase(Command.PING.getValue())){
            byte[] response = processCommand(line);
            return response;
        }
        return new byte[0];
    }

    private synchronized byte[] processCommand(String line) {
        CommandFactory commandFactory = new CommandFactory(line);
        CommandProcessor instance = commandFactory.getInstance();
        return instance.processCommand(line);
    }


}
