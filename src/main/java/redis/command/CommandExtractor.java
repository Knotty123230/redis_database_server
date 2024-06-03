package redis.command;

import java.io.BufferedReader;
import java.io.IOException;

public class CommandExtractor {

    public String getCommandMessage(BufferedReader bufferedReader){
        try {
            bufferedReader.readLine();
            String command = bufferedReader.readLine();
            System.out.println("Exctract command: " + command);
            return command;
        } catch (IOException e) {
            System.out.println("IOException CommandExtractor: " + e.getMessage());
            return "";
        }
    }
}
