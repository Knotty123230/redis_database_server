package redis.command.master;

import redis.command.CommandProcessor;
import redis.service.master.XaddStreamService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class XrangeCommandProcessor implements CommandProcessor {
    private final XaddStreamService xaddStreamService;

    public XrangeCommandProcessor() {
        xaddStreamService = XaddStreamService.getInstance();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        String name = command.removeFirst();
        List<Map<String, Map<String, String>>> valuesByStreamName = xaddStreamService.findValuesByStreamName(name, command);
        System.out.println(valuesByStreamName);
    }
}
