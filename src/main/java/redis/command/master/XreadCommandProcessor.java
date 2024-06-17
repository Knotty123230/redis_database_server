package redis.command.master;

import redis.command.CommandProcessor;
import redis.service.master.ExpireCommand;
import redis.service.master.StreamService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static redis.parser.CommandParser.writeResponse;

public class XreadCommandProcessor implements CommandProcessor {
    private final StreamService streamService;

    public XreadCommandProcessor() {
        streamService = StreamService.getInstance();
    }


    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        System.out.println("process xread command: " + command);
        String firstWord = command.removeFirst();
        List<String> names = new LinkedList<>();
        if (firstWord.equalsIgnoreCase("block")) {
            if (command.getLast().equalsIgnoreCase("$")) {
                String s = command.removeLast();
                System.out.println(command);
            }
            String time = command.removeFirst();
            firstWord = command.removeFirst();
            streamService.getExpireCommand().setReady(true);
            ExpireCommand expireCommand = streamService.getExpireCommand();
            while (!expireCommand.getIsAdded()) {
            }
        }
        if (!firstWord.equalsIgnoreCase("streams")) return;
        for (String s : command) {
            char[] charArray = s.toCharArray();
            char firsChar = charArray[0];
            if (Character.isDigit(firsChar)) break;

            names.add(s);
        }
        for (String name : names) {
            command.remove(name);
        }
        List<List<Map<String, Map<String, List<String>>>>> valuesByStreamNameBiggerThenId = streamService.findValuesByStreamNameBiggerThenId(names, command);
        String s = writeResponse(valuesByStreamNameBiggerThenId, os);
        os.write(s.getBytes());
        os.flush();
        streamService.getExpireCommand().setReady(false);
        streamService.getExpireCommand().isAdded(false);
    }
}
