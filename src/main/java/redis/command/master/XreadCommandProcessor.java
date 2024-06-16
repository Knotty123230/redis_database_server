package redis.command.master;

import redis.command.CommandProcessor;
import redis.service.master.StreamService;

import java.io.IOException;
import java.io.OutputStream;
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
        if (!streams.equalsIgnoreCase("streams"))return;
        String name = command.removeFirst();
        List<List<Map<String, Map<String, List<String>>>>> valuesByStreamNameBiggerThenId = streamService.findValuesByStreamNameBiggerThenId(name, command);
        writeResponse(valuesByStreamNameBiggerThenId, os);
    }
    public static void writeResponse(List<List<Map<String, Map<String, List<String>>>>> results, OutputStream os) throws IOException {
        os.write('*');
        os.write(String.valueOf(results.size()).getBytes());
        os.write("\r\n".getBytes());

        for (List<Map<String, Map<String, List<String>>>> streamResults : results) {
            os.write('*');
            os.write("2".getBytes());
            os.write("\r\n".getBytes());

            for (Map<String, Map<String, List<String>>> streamResult : streamResults) {
                for (Map.Entry<String, Map<String, List<String>>> entry : streamResult.entrySet()) {
                    os.write('$');
                    os.write(String.valueOf(entry.getKey().length()).getBytes());
                    os.write("\r\n".getBytes());
                    os.write(entry.getKey().getBytes());
                    os.write("\r\n".getBytes());

                    os.write('*');
                    os.write(String.valueOf(entry.getValue().size()).getBytes());
                    os.write("\r\n".getBytes());
                    os.write("*2".getBytes());
                    os.write("\r\n".getBytes());

                    for (Map.Entry<String, List<String>> idEntry : entry.getValue().entrySet()) {
                        os.write('$');
                        os.write(String.valueOf(idEntry.getKey().length()).getBytes());
                        os.write("\r\n".getBytes());
                        os.write(idEntry.getKey().getBytes());
                        os.write("\r\n".getBytes());

                        os.write('*');
                        os.write(String.valueOf(idEntry.getValue().size()).getBytes());
                        os.write("\r\n".getBytes());

                        for (String value : idEntry.getValue()) {
                            os.write('$');
                            os.write(String.valueOf(value.length()).getBytes());
                            os.write("\r\n".getBytes());
                            os.write(value.getBytes());
                            os.write("\r\n".getBytes());
                        }
                    }
                }
            }
        }
    }
}
