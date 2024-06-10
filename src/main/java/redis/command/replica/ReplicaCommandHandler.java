package redis.command.replica;

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
    @Override
    public boolean processCommand(List<String> commands, OutputStream os) {
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
}
