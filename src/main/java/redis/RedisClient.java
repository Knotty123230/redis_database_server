package redis;

import redis.command.CommandHandler;
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
    private final   CommandHandler commandHandler;

    public RedisClient(Socket socket) {
        this.socket = socket;
        this.replicaSender = ReplicaSender.getInstance();
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        if (applicationInfo.getInfo().get("role").equalsIgnoreCase(Role.MASTER.name())) {
            System.out.println("Cleate client for master node: " + socket.getLocalSocketAddress());
            Thread thread = new Thread(replicaSender);
            thread.start();
        }
        this.commandHandler = new CommandHandler(replicaSender);
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
            commandHandler.processCommand(parsedCommands, outputStream);
        }
    }

    private void addCommandThanSendsToReplica(List<String> command) {
        System.out.println("ADD commands that sends to replica: " + command);
        replicaSender.addCommand(commandParser.getResponseFromCommandArray(command));
    }





}
