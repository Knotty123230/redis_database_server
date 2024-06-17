package redis.command.sender;

import redis.model.Command;
import redis.parser.CommandParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplConfCommandSender extends CommandSender {
    private final String port;

    public ReplConfCommandSender(String port, CommandParser commandParser) {
        super(commandParser);
        this.port = port;
    }

    @Override
    public void sendCommand(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        String replconfResp = "";
        replconfResp = getReplconfResp(bufferedReader, outputStream);

        if (!replconfResp.isEmpty()) {
            replconfResp = sendReplConfCapa(bufferedReader, outputStream);
        }
        if (!replconfResp.isEmpty()) {
            outputStream.write(commandParser.getResponseFromCommandArray(List.of(Command.PSYNC.getValue(), "?", "-1")).getBytes());
        }
    }

    private String sendReplConfCapa(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        outputStream.write(commandParser.getResponseFromCommandArray(List.of(
                        Command.REPLCONF.getValue(),
                        "capa",
                        "npsync2")
                ).getBytes()
        );
        outputStream.flush();
        return bufferedReader.readLine();
    }

    private String getReplconfResp(BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        outputStream.write(commandParser.getResponseFromCommandArray(List.of(
                        Command.REPLCONF.getValue(),
                        "listening-port",
                        String.valueOf(port))
                ).getBytes()
        );
        outputStream.flush();
        return bufferedReader.readLine();
    }
}
