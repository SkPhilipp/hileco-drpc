package bot.demo.consumer.handlers;

import bot.demo.messages.Topic;
import bot.demo.messages.user.*;
import machine.lib.message.Network;
import machine.lib.message.TypedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserActionHandler implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(UserActionHandler.class);
    private String username;
    private Network network;

    public UserActionHandler(String username, Network network) {
        this.username = username;
        this.network = network;
    }

    public void start() {
        this.network.beginListen(Topic.USER_CHAT.with(username), UserActionHandler.this::chat);
        this.network.beginListen(Topic.USER_FARM.with(username), UserActionHandler.this::farm);
        this.network.beginListen(Topic.USER_INVENTORIZE.with(username), UserActionHandler.this::inventorize);
        this.network.beginListen(Topic.USER_LOCATE.with(username), UserActionHandler.this::locate);
        this.network.beginListen(Topic.USER_TRADE.with(username), UserActionHandler.this::trade);
    }

    @Override
    public void close() {
        this.network.stopListen(Topic.USER_CHAT.with(username));
        this.network.stopListen(Topic.USER_FARM.with(username));
        this.network.stopListen(Topic.USER_INVENTORIZE.with(username));
        this.network.stopListen(Topic.USER_LOCATE.with(username));
        this.network.stopListen(Topic.USER_TRADE.with(username));
    }

    public void chat(TypedMessage message) {
        ChatAction action = message.getContent(ChatAction.class);
        LOG.info("{}->{} chat \"{}\"", username, action.getTo(), action.getMessage());
    }

    public void farm(TypedMessage message) {
        FarmAction action = message.getContent(FarmAction.class);
        LOG.info("{} farm {}", username, action.getWhat());
    }

    public void inventorize(TypedMessage message) {
        InventorizeAction action = message.getContent(InventorizeAction.class);
        LOG.info("{} inventorize {}", username, action.getTradeables());
    }

    public void locate(TypedMessage message) {
        LOG.info("{} locate", username);
    }

    public void trade(TypedMessage message) {
        TradeAction action = message.getContent(TradeAction.class);
        LOG.info("{}->{} trade {}", username, action.getTo(), action.getTradeables());
    }

}
