package machine.lib.message.util;

import machine.lib.message.DelegatingMessageService;
import machine.lib.message.MessageHandler;
import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageHandlerBuilder<RES extends Serializable> {

    private List<MessageHandler<RES>> handlers;
    private List<Runnable> finishListeners;
    private Long timeout;
    private Integer limit;
    private DelegatingMessageService delegatingMessageService;

    public MessageHandlerBuilder(DelegatingMessageService delegatingMessageService) {
        this.delegatingMessageService = delegatingMessageService;
        this.handlers = new ArrayList<>();
        this.finishListeners = new ArrayList<>();
    }

    public MessageHandlerBuilder<RES> time(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    public MessageHandlerBuilder<RES> limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public MessageHandlerBuilder<RES> onFinish(Runnable element) {
        finishListeners.add(element);
        return this;
    }

    public MessageHandlerBuilder<RES> onReceive(MessageHandler<RES> handler) {
        this.handlers.add(handler);
        return this;
    }

    public <REQ extends Serializable> void beginCallback(final String topic, REQ content) {
        NetworkMessage<REQ> networkMessage = new NetworkMessage<>(topic, content);
        String callbackTopic = networkMessage.getMessageId().toString();
        BuiltMessageHandler<RES> handler = new BuiltMessageHandler<>(callbackTopic, delegatingMessageService, handlers, finishListeners, limit, timeout);
        delegatingMessageService.registerHandler(callbackTopic, handler);
        delegatingMessageService.publish(networkMessage);
    }

    public void begin(final String topic) {
        final BuiltMessageHandler<RES> handler = new BuiltMessageHandler<>(topic, delegatingMessageService, handlers, finishListeners, limit, timeout);
        delegatingMessageService.registerHandler(topic, handler);
    }

}
