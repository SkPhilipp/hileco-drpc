package machine.lib.message.util;

import machine.lib.message.LoadedTypedMessage;
import machine.lib.message.Network;
import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MessageHandlerBuilder<IN extends Serializable> {

    private List<Consumer<LoadedTypedMessage<IN>>> handlers;
    private List<Runnable> finishListeners;
    private Long timeout;
    private Integer limit;
    private Class<IN> responseClass;
    private Network network;

    public MessageHandlerBuilder(Class<IN> responseClass, Network network) {
        this.responseClass = responseClass;
        this.network = network;
        this.handlers = new ArrayList<>();
        this.finishListeners = new ArrayList<>();
    }

    public MessageHandlerBuilder<IN> time(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    public MessageHandlerBuilder<IN> limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public MessageHandlerBuilder<IN> onFinished(Runnable element) {
        finishListeners.add(element);
        return this;
    }

    public MessageHandlerBuilder<IN> onReceive(Consumer<LoadedTypedMessage<IN>> handler) {
        this.handlers.add(handler);
        return this;
    }

    public <OUT extends Serializable> MessageHandlerBuilder<IN> callback(final String topic, OUT content) {
        NetworkMessage<OUT> networkMessage = new NetworkMessage<>(topic, content);
        String callbackTopic = networkMessage.getMessageId().toString();
        MessageHandler<IN> handler = new MessageHandler<>(responseClass, callbackTopic, network, handlers, finishListeners, limit, timeout);
        network.beginListen(callbackTopic, handler);
        network.publishCustom(networkMessage);
        return this;
    }

    public MessageHandlerBuilder<IN> callback(final String topic) {
        return this.callback(topic, null);
    }

    public <OUT extends Serializable> MessageHandlerBuilder<IN> send(final String topic, OUT content) {
        NetworkMessage<OUT> networkMessage = new NetworkMessage<>(topic, content);
        network.publishCustom(networkMessage);
        return this;
    }

    public MessageHandlerBuilder<IN> send(final String topic) {
        return this.send(topic, null);
    }

    public MessageHandlerBuilder<IN> listen(final String topic) {
        final MessageHandler<IN> handler = new MessageHandler<>(responseClass, topic, network, handlers, finishListeners, limit, timeout);
        network.beginListen(topic, handler);
        return this;
    }

}
