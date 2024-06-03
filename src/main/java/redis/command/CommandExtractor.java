package redis.command;

import java.io.BufferedReader;
import java.io.IOException;

public class CommandExtractor {

    public String getCommandMessage(BufferedReader bufferedReader){
        try {
            bufferedReader.readLine();
            return bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("IOException CommandExtractor: " + e.getMessage());
            return "";
        }
    }
}
