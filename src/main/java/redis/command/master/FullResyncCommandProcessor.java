package redis.command.master;

import redis.command.CommandProcessor;
import redis.model.Command;
import redis.service.ApplicationInfo;
import redis.service.master.RdbFileInfo;
import redis.service.master.ReplicaSender;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FullResyncCommandProcessor implements CommandProcessor {
    private final ApplicationInfo applicationInfo;
    private final ReplicaSender replicaSender;
    private final RdbFileInfo rdbFileInfo;


    public FullResyncCommandProcessor(ReplicaSender replicaSender) {
        this.applicationInfo = ApplicationInfo.getInstance();
        this.replicaSender = replicaSender;
        rdbFileInfo = RdbFileInfo.getInstance();
    }

    @Override
    public void processCommand(List<String> command, OutputStream os) {
        synchronized (this) {
            replicaSender.addConnection(os);
            byte[] decode = rdbFileInfo.getContent();
            try {
                os.write(("+" + Command.FULLRESYNC.getValue() + " " + applicationInfo.getInfo().get("master_replid") + " 0\r\n").getBytes());
                os.write(("$" + decode.length + "\r\n").getBytes());
                os.write(decode);
                os.flush();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
