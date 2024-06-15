package redis.command.master;

import redis.command.CommandProcessor;
import redis.parser.CommandParser;
import redis.service.master.XaddStreamService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class XaddCommandProcessor implements CommandProcessor {
    private final XaddStreamService xaddStreamService;
    private final CommandParser commandParser;

    public XaddCommandProcessor() {
        xaddStreamService = XaddStreamService.getInstance();
        this.commandParser = new CommandParser();
    }

    public void processCommand(List<String> command, OutputStream os) throws IOException {
        System.out.println("Process xadd command: " + command);
        String nameOfStream = command.removeFirst();
        String id = command.removeFirst();
        String key = "";
        String value = "";

        for (int i = 0; i < command.size() - 1; i++) {
            key = command.get(i);
            value = command.get(i + 1);
        }
        String stream = xaddStreamService.createStream(nameOfStream, id, key, value);
        os.write(("$" + stream.length() + "\r\n" + stream + "\r\n").getBytes());
        os.flush();
    }

}
