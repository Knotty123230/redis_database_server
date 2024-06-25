package redis.command.master;

import redis.command.CommandProcessor;
import redis.model.Transaction;
import redis.service.master.TransactionMultiCommandService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MultiCommandProcessor implements CommandProcessor {
    private final TransactionMultiCommandService transactionMultiCommandService;

    public MultiCommandProcessor(TransactionMultiCommandService transactionMultiCommandService) {
        this.transactionMultiCommandService = transactionMultiCommandService;
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) throws IOException {
        transactionMultiCommandService.startTransaction(os);
        Transaction transaction = transactionMultiCommandService.getTransaction(os);
        transaction.setIsDiscard(false);
        transactionMultiCommandService.startTransaction(os);
        os.write("+OK\r\n".getBytes());
        os.flush();
    }
}
