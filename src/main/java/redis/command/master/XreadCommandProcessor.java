package redis.command.master;

import redis.command.CommandProcessor;
import redis.service.master.StreamService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XreadCommandProcessor implements CommandProcessor {
    private final StreamService streamService;

    public XreadCommandProcessor() {
        streamService = StreamService.getInstance();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        System.out.println("process xread command: " + command);
        String streams = command.removeFirst();
        List<String> names = new LinkedList<>();
        if (!streams.equalsIgnoreCase("streams")) return;
        for (String s : command) {
            char[] charArray = s.toCharArray();
            char firsChar = charArray[0];
            if (Character.isDigit(firsChar)) break;
            names.add(s);
        }
        for (String name : names) {
            command.remove(name);
        }
        List<List<Map<String, Map<String, List<String>>>>> valuesByStreamNameBiggerThenId = streamService.findValuesByStreamNameBiggerThenId(names, command);
        System.out.println(valuesByStreamNameBiggerThenId);
        writeResponse(valuesByStreamNameBiggerThenId, os);
    }

    public static void writeResponse(List<List<Map<String, Map<String, List<String>>>>> results, OutputStream os) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append('*').append(results.size()).append("\r\n");

        for (List<Map<String, Map<String, List<String>>>> streamResults : results) {
            sb.append('*').append("2").append("\r\n");

            for (Map<String, Map<String, List<String>>> streamResult : streamResults) {
                for (Map.Entry<String, Map<String, List<String>>> entry : streamResult.entrySet()) {
                    sb.append('$').append(entry.getKey().length()).append("\r\n");
                    sb.append(entry.getKey()).append("\r\n");

                    sb.append('*').append(entry.getValue().size()).append("\r\n");
                    sb.append('*').append("2").append("\r\n");
                    for (Map.Entry<String, List<String>> idEntry : entry.getValue().entrySet()) {
                        sb.append('$').append(idEntry.getKey().length()).append("\r\n");
                        sb.append(idEntry.getKey()).append("\r\n");

                        sb.append('*').append(idEntry.getValue().size()).append("\r\n");

                        for (String value : idEntry.getValue()) {
                            sb.append('$').append(value.length()).append("\r\n");
                            sb.append(value).append("\r\n");
                        }
                    }
                }
            }
        }
        System.out.println(sb);
        os.write(sb.toString().getBytes());
    }
}
