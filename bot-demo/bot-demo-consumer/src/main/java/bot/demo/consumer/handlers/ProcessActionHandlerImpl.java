package bot.demo.consumer.handlers;

import bot.demo.messages.ScanReply;
import bot.demo.messages.Topics;
import bot.demo.messages.process.LoginAction;
import bot.demo.messages.process.LogoutAction;
import bot.demo.messages.process.RegisterAction;
import machine.lib.message.MessageHandler;
import machine.lib.message.Network;
import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.UUID;

public class ProcessActionHandlerImpl implements ProcessActionHandler {

    // TODO: cleanup listen and handle system ...

    private UUID serverId;
    private Network network;

    public ProcessActionHandlerImpl(UUID serverId, Network network) {
        this.serverId = serverId;
        this.network = network;
    }

    public void start() {
        this.network.beginListen(Topics.SCAN, new MessageHandler<Serializable>() {
            @Override
            public void handle(NetworkMessage<?> message) {
                ProcessActionHandlerImpl.this.scan();
            }
        });
    }

    @Override
    public void login(LoginAction loginAction) {
        // TODO: implement
    }

    @Override
    public void logout(LogoutAction logoutAction) {
        // TODO: implement
    }

    @Override
    public void register(RegisterAction registerAction) {
        // TODO: implement
    }

    @Override
    public void scan() {
        ScanReply scanReply = new ScanReply();
        scanReply.setServerId(serverId);
        network.publishMessage(Topics.SCAN_REPLY, scanReply);
    }

}
