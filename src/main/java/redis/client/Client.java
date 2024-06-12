package redis.client;

import redis.parser.CommandParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public abstract class Client implements Runnable {
    protected final CommandParser commandParser;
    protected final Socket socket;
    protected BufferedReader reader;

    protected Client(Socket socket) {
        this.socket = socket;
        this.commandParser = new CommandParser();
    }

    protected Client(BufferedReader reader, Socket socket) {
        this.reader = reader;
        this.commandParser = new CommandParser();
        this.socket = socket;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = socket.getOutputStream()) {
            if (reader == null) {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            handleClient(reader, outputStream);
        } catch (IOException e) {
            System.out.println("IOException while handling client: " + e.getMessage());
        }
    }

    protected abstract void handleClient(BufferedReader bufferedReader, OutputStream outputStream) throws IOException;
}
