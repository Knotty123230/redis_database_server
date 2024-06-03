package redis.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandParser {
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
}