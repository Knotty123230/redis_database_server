package redis.command.master;

import redis.command.CommandProcessor;
import redis.command.model.ConfigCommand;
import redis.parser.CommandParser;
import redis.service.RdbFileInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ConfigCommandProcessor implements CommandProcessor {
    private final RdbFileInfo rdbFileInfo;
    private final CommandParser commandParser;

    public ConfigCommandProcessor() {
        rdbFileInfo = RdbFileInfo.getInstance();
        commandParser = new CommandParser();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        System.out.println("PROCESS COMMAND CONFIG");
        if (command.getFirst().equalsIgnoreCase(ConfigCommand.DIR.getValue())){
            String path = rdbFileInfo.getPath();
            os.write(commandParser.getResponseFromCommandArray(List.of("dir", path)).getBytes());
        } else if (command.getFirst().equalsIgnoreCase(ConfigCommand.DBFILENAME.getValue())) {
            String fileName = rdbFileInfo.getFileName();
            os.write(commandParser.getResponseFromCommandArray(List.of(ConfigCommand.DBFILENAME.name(), fileName)).getBytes());
        }
    }
}
