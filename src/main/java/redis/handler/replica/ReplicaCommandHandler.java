package redis.handler.replica;

import redis.command.CommandHandler;
import redis.command.CommandProcessor;
import redis.command.model.Command;
import redis.factory.Factory;
import redis.factory.replica.ReplicaCommandFactory;
import redis.utils.CommandUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplicaCommandHandler implements CommandHandler {
    private final List<String> commands;
    private final OutputStream os;

    public ReplicaCommandHandler(List<String> commands, OutputStream os) {
        this.commands = commands;
        this.os = os;
    }

    @Override
    public boolean processCommand() {
        System.out.println("RedisCommandHandler process command : " + commands);
        String remove = commands.removeFirst();
        Command command = CommandUtil.getCommand(remove);
        if (command == null) return true;
        Factory commandFactory = new ReplicaCommandFactory(command);
        CommandProcessor commandProcessor = commandFactory.getInstance();
        try {
            commandProcessor.processCommand(commands, os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    @Override
    public void run() {
        processCommand();
    }
}
