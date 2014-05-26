package machine.lib.message.util;

import machine.lib.message.DelegatingMessageService;
import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageHandlerBuilder<RES extends Serializable> {

    private List<MessageProcessor<RES>> handlers;
    private List<Runnable> finishListeners;
    private Long timeout;
    private Integer limit;
    private Class<RES> responseClass;
    private DelegatingMessageService delegatingMessageService;

    public MessageHandlerBuilder(Class<RES> responseClass, DelegatingMessageService delegatingMessageService) {
        this.responseClass = responseClass;
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

    public MessageHandlerBuilder<RES> onFinished(Runnable element) {
        finishListeners.add(element);
        return this;
    }

    public MessageHandlerBuilder<RES> onReceive(MessageProcessor<RES> handler) {
        this.handlers.add(handler);
        return this;
    }

    public <REQ extends Serializable> MessageHandlerBuilder<RES> callback(final String topic, REQ content) {
        NetworkMessage<REQ> networkMessage = new NetworkMessage<>(topic, content);
        String callbackTopic = networkMessage.getMessageId().toString();
        MessageHandler<RES> handler = new MessageHandler<>(responseClass, callbackTopic, delegatingMessageService, handlers, finishListeners, limit, timeout);
        delegatingMessageService.beginListen(callbackTopic, handler);
        delegatingMessageService.publishCustom(networkMessage);
        return this;
    }

    public MessageHandlerBuilder<RES> callback(final String topic) {
        return this.callback(topic, null);
    }

    public <REQ extends Serializable> MessageHandlerBuilder<RES> send(final String topic, REQ content) {
        NetworkMessage<REQ> networkMessage = new NetworkMessage<>(topic, content);
        delegatingMessageService.publishCustom(networkMessage);
        return this;
    }

    public MessageHandlerBuilder<RES> send(final String topic) {
        return this.send(topic, null);
    }

    public MessageHandlerBuilder<RES> listen(final String topic) {
        final MessageHandler<RES> handler = new MessageHandler<>(responseClass, topic, delegatingMessageService, handlers, finishListeners, limit, timeout);
        delegatingMessageService.beginListen(topic, handler);
        return this;
    }

}
