package redis.command.master;

import redis.command.CommandProcessor;
import redis.service.master.TransactionMultiCommandService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DiscardCommandProcessor implements CommandProcessor {
    private final TransactionMultiCommandService transactionMultiCommandService;

    public DiscardCommandProcessor(TransactionMultiCommandService transactionMultiCommandService) {
        this.transactionMultiCommandService = transactionMultiCommandService;
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        if (this.transactionMultiCommandService.isMulti()){
            transactionMultiCommandService.setIsDiscard(true);
            transactionMultiCommandService.stopTransaction();
            os.write("+OK\r\n".getBytes());
            os.flush();
            return;
        }
        os.write("-ERR DISCARD without MULTI\r\n".getBytes());
        os.flush();

    }
}
