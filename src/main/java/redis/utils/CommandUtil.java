package redis.utils;

import redis.command.model.Command;

public class CommandUtil {
    public static Command getCommand(String remove) {
        System.out.println("CommandUtil : getCommand: " + remove);
        if (remove.toLowerCase().contains(Command.ECHO.getValue().toLowerCase())) {
            return Command.ECHO;
        } else if (remove.toLowerCase().contains(Command.PING.getValue().toLowerCase())) {
            return Command.PING;
        } else if (remove.toLowerCase().contains(Command.SET.getValue().toLowerCase())) {
            return Command.SET;
        } else if (remove.toLowerCase().contains(Command.GET.getValue().toLowerCase())) {
            return Command.GET;
        } else if (remove.toLowerCase().contains(Command.INFO.getValue().toLowerCase())) {
            return Command.INFO;
        } else if (remove.toLowerCase().contains(Command.REPLCONF.getValue().toLowerCase()) || remove.equalsIgnoreCase(Command.REPLCONF.getValue())) {
            return Command.REPLCONF;
        } else if (remove.toLowerCase().contains(Command.FULLRESYNC.getValue().toLowerCase()) || remove.equalsIgnoreCase(Command.FULLRESYNC.getValue())) {
            return Command.FULLRESYNC;
        } else if (remove.toLowerCase().contains(Command.PSYNC.getValue()) || remove.equalsIgnoreCase(Command.PSYNC.getValue())) {
            return Command.PSYNC;
        } else if (remove.toLowerCase().contains(Command.WAIT.getValue()) || remove.equalsIgnoreCase(Command.WAIT.getValue())) {
            return Command.WAIT;
        } else if (remove.toLowerCase().contains(Command.CONFIG.getValue()) || remove.equalsIgnoreCase(Command.CONFIG.getValue())) {
            return Command.CONFIG;
        } else return null;
    }
}
