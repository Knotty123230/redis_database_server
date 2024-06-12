package redis.service;

import java.io.BufferedReader;
import java.io.IOException;

public class RdbReader {
    public BufferedReader read(BufferedReader bf) {
        try {


            String s = bf.readLine();
            String substring = s.substring(1);
            int charsToSkip = Integer.parseInt(substring) - 1;
            long skip = bf.skip(charsToSkip);
            System.out.println("skipped bytes : " + skip);
            return bf;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
