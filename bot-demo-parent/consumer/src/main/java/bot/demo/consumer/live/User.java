package bot.demo.consumer.live;

import machine.drcp.core.api.Client;
import machine.drcp.core.api.util.Listener;
import machine.drcp.core.api.util.SilentCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class User extends Listener implements LiveUser {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);
    private String username;
    private String password;
    private Client client;

    public User(String username, String password, Client client) {
        this.username = username;
        this.password = password;
        this.client = client;
    }

    @Override
    protected SilentCloseable listen() {
        return this.client.listen(LiveUser.class, this, username);
    }

    @Override
    public void chat(String receiver, String message) {
        LOG.info("{}->{} chat \"{}\"", username, receiver, message);
    }

    @Override
    public void farm(String what) {
        LOG.info("{} farm {}", username, what);
    }

    @Override
    public void inventorize(List<String> tradeables) {
        LOG.info("{} inventorize {}", username, tradeables);
    }

    @Override
    public void locate() {
        LOG.info("{} locate", username);
    }

    @Override
    public void trade(String to, List<String> tradeables) {
        LOG.info("{}->{} trade {}", username, to, tradeables);
    }

}
