package redis.handler.master;

import redis.command.CommandHandler;
import redis.command.CommandProcessor;
import redis.command.model.Command;
import redis.factory.master.MasterCommandFactory;
import redis.parser.CommandParser;
import redis.service.ReplicaSender;
import redis.utils.CommandUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MasterCommandHandler implements CommandHandler {
    private final ReplicaSender replicaSender;
    private final CommandParser commandParser;

    public MasterCommandHandler(ReplicaSender replicaSender) {
        this.replicaSender = replicaSender;
        replicaSender.start();
        this.commandParser = new CommandParser();
    }


    public synchronized boolean processCommand(List<String> commands, OutputStream os) {
        ArrayList<String> replicaCommand = new ArrayList<>(commands);
        String remove = commands.removeFirst();
        Command command = CommandUtil.getCommand(remove);
        if (command == null) return true;
        replicaSender.addCommand(commandParser.getResponseFromCommandArray(replicaCommand));
        System.out.println("MasterCommandHandler processCommand: " + Objects.requireNonNull(command).getValue());
        MasterCommandFactory commandFactory = new MasterCommandFactory(command, replicaSender);
        CommandProcessor commandProcessor = commandFactory.getInstance();
        try {
            commandProcessor.processCommand(commands, os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
