package redis.handler.master;

import redis.command.CommandHandler;
import redis.command.CommandProcessor;
import redis.command.model.Command;
import redis.factory.Factory;
import redis.factory.master.MasterCommandFactory;
import redis.parser.CommandParser;
import redis.service.master.ReplicaReceiver;
import redis.service.master.ReplicaSender;
import redis.utils.CommandUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MasterCommandHandler implements CommandHandler {
    private final ReplicaSender replicaSender;
    private final CommandParser commandParser;
    private final List<String> commands;
    private final OutputStream os;
    private final ReplicaReceiver replicaReceiver;

    public MasterCommandHandler(ReplicaReceiver replicaReceiver, ReplicaSender replicaSender, List<String> commands, OutputStream outputStream) {
        this.replicaSender = replicaSender;
        this.commands = commands;
        this.os = outputStream;
        replicaSender.start();
        this.commandParser = new CommandParser();
        this.replicaReceiver = replicaReceiver;
    }


    public synchronized boolean processCommand() {
        ArrayList<String> replicaCommand = new ArrayList<>(commands);
        String remove = commands.removeFirst();
        Command command = CommandUtil.getCommand(remove);
        if (command == null) return true;
        System.out.println("MasterCommandHandler processCommand: " + Objects.requireNonNull(command).getValue());
        Factory commandFactory = new MasterCommandFactory(command, replicaSender, replicaReceiver);
        CommandProcessor commandProcessor = commandFactory.getInstance();
        try {
            commandProcessor.processCommand(commands, os);
            replicaSender.addCommand(commandParser.getResponseFromCommandArray(replicaCommand));
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
