package bot.demo.consumer.handlers;

import bot.demo.messages.Topic;
import bot.demo.messages.process.*;
import machine.lib.message.Network;
import machine.lib.message.TypedMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProcessActionHandler {

    private Map<String, UserActionHandler> userActionHandlers;
    private UUID processId;
    private Network network;

    public ProcessActionHandler(UUID processId, Network network) {
        this.userActionHandlers = new HashMap<>();
        this.processId = processId;
        this.network = network;
    }

    public void start() {
        this.network.beginListen(Topic.PROCESS_SCAN.toString(), this::scan);
        this.network.beginListen(Topic.PROCESS_LOGIN.with(processId), this::login);
        this.network.beginListen(Topic.PROCESS_LOGOUT.with(processId), this::logout);
        this.network.beginListen(Topic.PROCESS_REGISTER.with(processId), this::register);
    }

    public void stop() {
        this.network.stopListen(Topic.PROCESS_SCAN.toString());
        this.network.stopListen(Topic.PROCESS_LOGIN.with(processId));
        this.network.stopListen(Topic.PROCESS_LOGOUT.with(processId));
        this.network.stopListen(Topic.PROCESS_REGISTER.with(processId));
    }

    public void login(TypedMessage message) {
        LoginAction loginAction = message.getContent(LoginAction.class);
        String username = loginAction.getUsername();
        UserActionHandler userActionHandler = new UserActionHandler(username, this.network);
        userActionHandler.start();
        this.userActionHandlers.put(username, userActionHandler);
        LoginReply loginReply = new LoginReply();
        loginReply.setServerId(this.processId);
        loginReply.setUsername(username);
        this.network.publishMessage(message.getMessageId(), loginReply);
    }

    public void logout(TypedMessage message) {
        LogoutAction logoutAction = message.getContent(LogoutAction.class);
        String username = logoutAction.getUsername();
        if (this.userActionHandlers.containsKey(username)) {
            UserActionHandler userActionHandler = this.userActionHandlers.get(username);
            userActionHandler.stop();
            this.userActionHandlers.remove(username);
        }
    }

    public void register(TypedMessage message) {
        String username = UUID.randomUUID().toString();
        UserActionHandler userActionHandler = new UserActionHandler(username, this.network);
        userActionHandler.start();
        this.userActionHandlers.put(username, userActionHandler);
        RegisterReply registerReply = new RegisterReply();
        registerReply.setServerId(this.processId);
        registerReply.setUsername(username);
        this.network.publishMessage(message.getMessageId(), registerReply);
    }

    public void scan(TypedMessage message) {
        ScanReply scanReply = new ScanReply();
        scanReply.setServerId(this.processId);
        this.network.publishMessage(Topic.PROCESS_SCAN_REPLY.toString(), scanReply);
    }

}
