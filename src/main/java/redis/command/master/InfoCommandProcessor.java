package redis.command.master;

import redis.command.CommandProcessor;
import redis.model.Command;
import redis.service.ApplicationInfo;
import redis.utils.ResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InfoCommandProcessor implements CommandProcessor {
    private final ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

    @Override
    public void processCommand(List<String> commands, OutputStream os) {
        System.out.println(applicationInfo.getInfo());
        String command = commands.getFirst();
        if (command.equalsIgnoreCase(Command.REPLICATION.getValue())) {
            Map<String, String> info = applicationInfo.getInfo();
            String response = info.entrySet()
                    .stream()
                    .map(data -> data.getKey() + ":" + data.getValue())
                    .collect(Collectors.joining());
            try {
                os.write(ResponseUtil.getResponse(response));
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
