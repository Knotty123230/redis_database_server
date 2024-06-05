package redis.command;

import redis.command.model.Command;
import redis.service.ApplicationInfo;

import java.util.List;

public class FullResyncCommandProcessor implements CommandProcessor {
    private final ApplicationInfo applicationInfo;

    public FullResyncCommandProcessor() {
        this.applicationInfo = ApplicationInfo.getInstance();
    }

    @Override
    public byte[] processCommand(List<String> command) {
        return ("+" + Command.FULLRESYNC.getValue() + applicationInfo.getInfo().get("master_replid") + "0\r\n").getBytes();
    }
}
