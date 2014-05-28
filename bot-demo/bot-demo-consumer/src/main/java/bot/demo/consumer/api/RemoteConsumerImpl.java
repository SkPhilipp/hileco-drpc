package bot.demo.consumer.api;

import bot.demo.master.api.RemoteMaster;
import machine.lib.message.api.NetworkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class RemoteConsumerImpl implements RemoteConsumer, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteConsumerImpl.class);

    private final NetworkConnector networkConnector;
    private final UUID processId;
    private RemoteMaster remoteMaster;

    public RemoteConsumerImpl(UUID processId, NetworkConnector networkConnector) {
        this.processId = processId;
        this.networkConnector = networkConnector;
        this.remoteMaster = this.networkConnector.remote(RemoteMaster.class);
    }

    public void start() {
        this.networkConnector.listen(RemoteConsumer.class, this);
    }

    @Override
    public void close() throws Exception {
        this.networkConnector.endListen(this);
    }

    @Override
    public void notifyScan() {
        LOG.info("{} notified of scan", processId);
        this.remoteMaster.completedScan(processId);
    }

}
