package redis;

import redis.command.CommandHandler;
import redis.command.model.Command;
import redis.command.replica.CommandByteCounter;
import redis.parser.CommandParser;
import redis.service.ReplicaSender;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class RedisClient implements Runnable {
    private final Socket socket;
    private final CommandParser commandParser = new CommandParser();
    private final ReplicaSender replicaSender;
    private final CommandHandler commandHandler;
    private final BufferedReader reader;
    private CommandByteCounter commandByteCounter;

    public RedisClient(Socket socket) {
        this.socket = socket;
        this.replicaSender = ReplicaSender.getInstance();
        Thread thread = new Thread(replicaSender);
        thread.start();
        this.commandHandler = new CommandHandler(replicaSender);
        reader = null;
    }

    public RedisClient(Socket socket, BufferedReader bufferedReader) {
        this.socket = socket;
        this.replicaSender = ReplicaSender.getInstance();
        this.commandHandler = new CommandHandler(null);
        this.reader = bufferedReader;
        this.commandByteCounter = CommandByteCounter.getInstance();
    }

    @Override
    public void run() {

        try (OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream();
             BufferedReader bufferedReader = reader == null ? new BufferedReader(new InputStreamReader(inputStream)) : reader) {
            handleClient(bufferedReader, outputStream);
        } catch (IOException e) {
            System.out.println("IOException while handling client: " + e.getMessage());
        }
    }

    private void handleClient(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.printf("line read by handleClient: %s%n", line);
            if (line.isEmpty()) continue;

            List<String> parsedCommands = commandParser.parseCommand(bufferedReader, line);
            if (reader != null){

                if (commandByteCounter.isFirst()){
                    commandByteCounter.setIsFirst(false);
                }else {
                    int length = commandParser.getResponseFromCommandArray(parsedCommands).getBytes().length;
                    System.out.println(parsedCommands);
                    System.out.println(length);
                    commandByteCounter.addBytes(length);
                }

            }
            addCommandThanSendsToReplica(parsedCommands);
            System.out.println("PARSED COMMANDS : " + parsedCommands);
            commandHandler.processCommand(parsedCommands, outputStream);
        }
    }

    private void addCommandThanSendsToReplica(List<String> command) {
        System.out.println("ADD commands that sends to replica: " + command);

        replicaSender.addCommand(commandParser.getResponseFromCommandArray(command));
    }


}
