package bot.demo.consumer.api;

import machine.lib.message.api.NetworkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RemoteUserImpl implements RemoteUser, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteUserImpl.class);
    private String username;
    private NetworkConnector networkConnector;

    public RemoteUserImpl(String username, NetworkConnector networkConnector) {
        this.username = username;
        this.networkConnector = networkConnector;
    }

    public void start() {
        this.networkConnector.listen(RemoteUser.class, this, username);
    }

    @Override
    public void close() {
        this.networkConnector.endListen(this, username);
    }

    @Override
    public void doChat(String receiver, String message) {
        LOG.info("{}->{} chat \"{}\"", username, receiver, message);
    }

    @Override
    public void doFarm(String what) {
        LOG.info("{} farm {}", username, what);
    }

    @Override
    public void doInventorize(List<String> tradeables) {
        LOG.info("{} inventorize {}", username, tradeables);
    }

    @Override
    public void doLocate() {
        LOG.info("{} locate", username);
    }

    @Override
    public void doTrade(String to, List<String> tradeables) {
        LOG.info("{}->{} trade {}", username, to, tradeables);
    }

}
