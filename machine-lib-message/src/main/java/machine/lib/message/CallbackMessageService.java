package machine.lib.message;

import machine.management.api.entities.Subscriber;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import machine.message.api.exceptions.NotSubscribedException;
import machine.message.api.services.MessageService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of MessageService that delegates message handlers to registered callbacks for handlerMap.
 */
public class CallbackMessageService implements MessageService {

    public Map<String, CallbackHandler> handlerMap;
    private Integer localPort;
    private NetworkService networkService;

    /**
     * @param localPort port over which this service is made available
     * @param networkService networkservice on which to subscribe and publish messages over
     */
    public CallbackMessageService(Integer localPort, NetworkService networkService) {
        this.localPort = localPort;
        this.networkService = networkService;
        this.handlerMap = new HashMap<>();
    }

    /**
     * Handles a message by delegating it to the internal {@link #handlerMap}.
     *
     * @param instance message to handle
     * @throws NotSubscribedException when there is no registered callback handler
     */
    @Override
    public void handle(NetworkMessage<?> instance) throws NotSubscribedException {
        String topic = instance.getTopic();
        CallbackHandler handler = this.handlerMap.get(topic);
        if (handler != null) {
            handler.handle(instance);
        } else {
            throw new NotSubscribedException();
        }
    }

    /**
     * Registers a callback, instantiates a new {@link NetworkMessage} and begins listening on the randomly
     * generated message id; Any incoming messages with topic being that message id will be delegated to the handler
     * until the handler is unregistered.
     *
     *
     * @param topic the message topic
     * @param content the message content
     * @param handler the callback handler
     * @return subscriber object returned by the networkservice
     */
    public <T extends Serializable> Subscriber beginCallback(String topic, T content, CallbackHandler handler){
        // create NetworkMessage and Subscriber object
        NetworkMessage<T> networkMessage = new NetworkMessage<>(topic, content);
        Subscriber subscriber = this.beginListen(networkMessage.getMessageId().toString(), handler);
        this.networkService.publish(networkMessage);
        return subscriber;
    }

    /**
     * Registers a callback. Any incoming messages with topic being that message id will be delegated to the handler
     * until the handler is unregistered.
     *
     * @param topic the message topic
     * @param handler the callback handler
     * @return subscriber object returned by the networkservice
     */
    public <T extends Serializable> Subscriber beginListen(String topic, CallbackHandler handler){
        // create NetworkMessage and Subscriber object
        Subscriber callback = new Subscriber();
        callback.setPort(this.localPort);
        callback.setTopic(topic);
        // subscribe to the message id, and publish the message
        Subscriber subscriber = this.networkService.save(callback);
        this.handlerMap.put(topic, handler);
        return subscriber;
    }

    /**
     * Removes a callbackhandler from the internal {@link #handlerMap} and deletes the subcription.
     *
     * @param subscriber the subscription to remove.
     */
    public void stopSubscription(Subscriber subscriber){
        this.handlerMap.remove(subscriber.getTopic());
        this.networkService.delete(subscriber.getId());
    }

}
