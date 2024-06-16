package redis.command.master;

import redis.command.CommandProcessor;
import redis.service.master.XaddStreamService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class XrangeCommandProcessor implements CommandProcessor {
    private final XaddStreamService xaddStreamService;

    public XrangeCommandProcessor() {
        xaddStreamService = XaddStreamService.getInstance();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        System.out.println("Process xrange command: " + command);
        String name = command.removeFirst();
        List<Map<String, List<String>>> allKeys = null;
        if (command.getFirst().equals("-")) {
            String minus = command.removeFirst();
            allKeys = xaddStreamService.findValuesNotBiggerThenId(name, command);
        } else {
            allKeys = xaddStreamService.findValuesByStreamName(name, command);
        }

        StringBuilder sb = new StringBuilder();

        sb.append("*").append(Objects.requireNonNull(allKeys).size()).append("\r\n");
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
