package redis.command.master;

import redis.command.CommandProcessor;
import redis.parser.CommandParser;
import redis.service.master.XaddStreamService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class XrangeCommandProcessor implements CommandProcessor {
    private final XaddStreamService xaddStreamService;
    private final CommandParser commandParser;

    public XrangeCommandProcessor() {
        xaddStreamService = XaddStreamService.getInstance();
        commandParser = new CommandParser();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        String name = command.removeFirst(); // Corrected from removeFirst() to remove(0)
        List<Map<String, List<String>>> allKeys = xaddStreamService.findValuesByStreamName(name, command);
        StringBuilder sb = new StringBuilder();

        sb.append("*").append(allKeys.size()).append("\r\n");
        for (Map<String, List<String>> entry : allKeys) {
            for (Map.Entry<String, List<String>> item : entry.entrySet()) {
                sb.append("*2\r\n");
                sb.append("$").append(item.getKey().length()).append("\r\n");
                sb.append(item.getKey()).append("\r\n");
                sb.append("*").append(item.getValue().size()).append("\r\n");
                for (String subEntry : item.getValue()) {
                    sb.append("$").append(subEntry.length()).append("\r\n");
                    sb.append(subEntry).append("\r\n");
                }
            }
        }

        System.out.println(sb.toString());
        os.write(sb.toString().getBytes());
        os.flush();
    }
}
