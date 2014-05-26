package bot.demo.consumer.handlers;

import bot.demo.messages.Topic;
import bot.demo.messages.user.*;
import machine.lib.message.Network;
import machine.lib.message.TypedMessage;

public class UserActionHandler {

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

    public void stop() {
        this.network.stopListen(Topic.USER_CHAT.with(username));
        this.network.stopListen(Topic.USER_FARM.with(username));
        this.network.stopListen(Topic.USER_INVENTORIZE.with(username));
        this.network.stopListen(Topic.USER_LOCATE.with(username));
        this.network.stopListen(Topic.USER_TRADE.with(username));
    }

    public void chat(TypedMessage message) {
        ChatAction action = message.getContent(ChatAction.class);
        // TODO: implement
    }

    public void farm(TypedMessage message) {
        FarmAction action = message.getContent(FarmAction.class);
        // TODO: implement
    }

    public void inventorize(TypedMessage message) {
        InventorizeAction action = message.getContent(InventorizeAction.class);
        // TODO: implement
    }

    public void locate(TypedMessage message) {
        LocateAction action = message.getContent(LocateAction.class);
        // TODO: implement
    }

    public void trade(TypedMessage message) {
        TradeAction action = message.getContent(TradeAction.class);
        // TODO: implement
    }

}
