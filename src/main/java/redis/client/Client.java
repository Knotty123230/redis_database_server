package redis.client;

import redis.command.CommandHandler;
import redis.parser.CommandParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public abstract class Client implements Runnable {
    protected BufferedReader reader;
    protected final CommandHandler commandHandler;
    protected final CommandParser commandParser;
    protected final Socket socket;

    protected Client(Socket socket, CommandHandler commandHandler) {
        this.socket = socket;
        this.commandHandler = commandHandler;
        this.commandParser = new CommandParser();
    }

    protected Client(BufferedReader reader, CommandHandler commandHandler, Socket socket) {
        this.reader = reader;
        this.commandHandler = commandHandler;
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
