package redis;

import java.io.*;
import java.net.Socket;

public class RedisClient implements Runnable {
    private final Socket socket;

    public RedisClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream();
        ) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                    byte[] response = processRequest(line);
                    if (response.length == 0) continue;
                    outputStream.write(response);
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static byte[] processRequest(String line) {
        if (line.equalsIgnoreCase(Command.PING.getValue())) {
            return getResponse();

        }
        return new byte[]{};
    }

    private static byte[] getResponse() {
        return ("+" + Command.PONG.getValue() + "\r\n").getBytes();
    }
}
