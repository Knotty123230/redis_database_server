package redis.command;

import redis.Command;
import redis.service.ApplicationInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InfoCommandProcessor implements CommandProcessor{
    private final ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
    @Override
    public byte[] processCommand(List<String> commands) {
        String command = commands.getFirst();
        if (command.equalsIgnoreCase(Command.REPLICATION.getValue())){
            Map<String, String> info = applicationInfo.getInfo();
            String response = info.entrySet()
                    .stream()
                    .map(data -> data.getKey() + ":" + data.getValue())
                    .collect(Collectors.joining());
        return ResponseUtil.getResponse(response);
        }

        return new byte[0];
    }
}
