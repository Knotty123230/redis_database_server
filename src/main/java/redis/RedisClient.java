package redis;

import redis.command.CommandProcessor;
import redis.command.model.Command;
import redis.factory.CommandFactory;
import redis.model.Role;
import redis.parser.CommandParser;
import redis.service.ApplicationInfo;
import redis.service.ReplicaSender;
import redis.utils.CommandUtil;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class RedisClient implements Runnable {
    private final Socket socket;
    private final CommandParser commandParser = new CommandParser();
    private final ReplicaSender replicaSender;

    public RedisClient(Socket socket) {
        this.socket = socket;
        this.replicaSender = ReplicaSender.getInstance();
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        if (applicationInfo.getInfo().get("role").equalsIgnoreCase(Role.MASTER.name())) {
            Thread thread = new Thread(replicaSender);
            thread.start();
        }
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
            List<String> parsedCommands = commandParser.parseCommand(bufferedReader, line);
            addCommandThanSendsToReplica(parsedCommands);
            System.out.println("PARSED COMMANDS : " + parsedCommands);
            processCommand(parsedCommands, outputStream);
        }
    }

    private void addCommandThanSendsToReplica(List<String> command) {
        System.out.println("ADD commands that sends to replica: " + command);
        replicaSender.addCommand(commandParser.getResponseFromCommandArray(command));
    }


    private synchronized void processCommand(List<String> commands, OutputStream os) {
        String remove = commands.removeFirst();
        Command command = CommandUtil.getCommand(remove);
        System.out.println("RedisClient processCommand: " + Objects.requireNonNull(command).getValue());
        CommandFactory commandFactory = new CommandFactory(command, replicaSender);
        CommandProcessor commandProcessor = commandFactory.getInstance();
        commandProcessor.processCommand(commands, os);
    }


}
