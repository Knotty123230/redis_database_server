package redis.command;

import redis.command.model.Command;
import redis.factory.CommandFactory;
import redis.service.ReplicaSender;
import redis.utils.CommandUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class CommandHandler {
    private final ReplicaSender replicaSender;

    public CommandHandler(ReplicaSender replicaSender) {
        this.replicaSender = replicaSender;
    }


    public synchronized boolean processCommand(List<String> commands, OutputStream os) {
        String remove = commands.removeFirst();
        Command command = CommandUtil.getCommand(remove);
        if (command == null) return true;
        System.out.println("RedisClient processCommand: " + Objects.requireNonNull(command).getValue());
        CommandFactory commandFactory;
        if (replicaSender == null) {
            commandFactory = new CommandFactory(command);
        } else {
            commandFactory = new CommandFactory(command, replicaSender);
        }
        CommandProcessor commandProcessor = commandFactory.getInstance();
        try {
            commandProcessor.processCommand(commands, os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
