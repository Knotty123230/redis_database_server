package redis.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandParser {

    public static String writeResponse(List<List<Map<String, Map<String, List<String>>>>> results, OutputStream os) throws IOException {
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
                    if (entry.getValue().isEmpty()) return "$-1\r\n";
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
        return sb.toString();
    }

    public List<String> parseCommand(BufferedReader bufferedReader, String line) {
        try {
            ArrayList<String> commands = new ArrayList<>();
            if (line.startsWith("*")) {
                String substring = line.substring(1);
                System.out.println("Array of commands length: " + substring);
                for (int i = 0; i < Integer.parseInt(substring); i++) {
                    bufferedReader.readLine();
                    String nextCommand = bufferedReader.readLine();
                    commands.add(nextCommand);
                }
            } else commands.add(line);
            System.out.println("Parsing commands: " + commands);
            return commands;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getResponseFromCommandArray(List<String> command) {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(command.size()).append("\r\n");
        for (String s : command) {
            sb.append("$").append(s.length()).append("\r\n").append(s).append("\r\n");
        }
        return sb.toString();
    }
}
