package redis.command;

import redis.Command;

public class CommandUtil {
    public static Command getCommand(String remove) {
        if (remove.toLowerCase().contains(Command.ECHO.getValue().toLowerCase())) {
            return Command.ECHO;
        } else if (remove.toLowerCase().contains(Command.PING.getValue().toLowerCase())) {
            return Command.PING;
        } else if (remove.toLowerCase().contains(Command.SET.getValue().toLowerCase())) {
            return Command.SET;
        } else if (remove.toLowerCase().contains(Command.GET.getValue().toLowerCase())) {
            return Command.GET;
        }else if (remove.toLowerCase().contains(Command.INFO.getValue().toLowerCase())) {
            return Command.INFO;
        } else return null;
    }
}
