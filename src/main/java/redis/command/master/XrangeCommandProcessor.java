package redis.command.master;

import redis.command.CommandProcessor;
import redis.service.master.StreamService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class XrangeCommandProcessor implements CommandProcessor {
    private final StreamService streamService;

    public XrangeCommandProcessor() {
        streamService = StreamService.getInstance();
    }

    private static StringBuilder getResponse(List<Map<String, List<String>>> allKeys) {
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
        return sb;
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        System.out.println("Process xrange command: " + command);
        String name = command.removeFirst();
        List<Map<String, List<String>>> allKeys;
        allKeys = findKeys(command, name);
        StringBuilder sb = getResponse(allKeys);

        System.out.println(sb);
        os.write(sb.toString().getBytes());
        os.flush();
    }

    private List<Map<String, List<String>>> findKeys(List<String> command, String name) {
        List<Map<String, List<String>>> allKeys;
        if (command.getFirst().equals("-")) {
            String minus = command.removeFirst();
            allKeys = streamService.findValuesNotBiggerThenId(name, command);
        } else if (command.getLast().equals("+")) {
            String s = command.removeLast();
            allKeys = streamService.findValuesBiggerThenId(name, command);
        } else {
            allKeys = streamService.findValuesByStreamName(name, command);
        }
        return allKeys;
    }
}
