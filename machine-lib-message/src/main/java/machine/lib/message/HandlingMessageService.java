package machine.lib.message;

import machine.management.api.entities.Subscriber;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import machine.message.api.exceptions.NotSubscribedException;
import machine.message.api.services.MessageService;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * An implementation of MessageService that delegates message handlers to registered callbacks for handlerMap.
 */
public class HandlingMessageService implements MessageService {

    private static final Logger LOG = getLogger(HandlingMessageService.class);

    public Map<String, NetworkMessageListener> handlerMap;
    private Integer localPort;
    private NetworkService networkService;

    /**
     * @param localPort      port over which this service is made available
     * @param networkService networkservice on which to subscribe and publish messages over
     */
    public HandlingMessageService(Integer localPort, NetworkService networkService) {
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
    @SuppressWarnings("unchecked")
    @Override
    public void handle(NetworkMessage<?> instance) throws NotSubscribedException {
        String topic = instance.getTopic();
        NetworkMessageListener handler = this.handlerMap.get(topic);
        if (handler != null) {
            LOG.trace("Handling message with topic {} and id {}", instance.getTopic(), instance.getMessageId());
            handler.handle(instance);
        } else {
            LOG.trace("Ignoring message with topic {} and id {}", instance.getTopic(), instance.getMessageId());
            throw new NotSubscribedException();
        }
    }

    /**
     * Registers a callback, instantiates a new {@link NetworkMessage} and begins listening on the randomly
     * generated message id; Any incoming messages with topic being that message id will be delegated to the handler
     * until the handler is unregistered.
     *
     * @param topic   the message topic
     * @param content the message content
     * @param handler the callback handler
     * @return subscriber object returned by the networkservice
     */
    public <T extends Serializable> Subscriber beginCallback(String topic, T content, NetworkMessageListener<?> handler) {
        // create NetworkMessage and Subscriber object
        NetworkMessage<T> networkMessage = new NetworkMessage<>(topic, content);
        Subscriber subscriber = this.beginListen(networkMessage.getMessageId().toString(), handler);
        this.networkService.publish(networkMessage);
        return subscriber;
    }

    /**
     * Registers a callback, instantiates a new contentless {@link NetworkMessage} and begins listening on the randomly
     * generated message id; Any incoming messages with topic being that message id will be delegated to the handler
     * until the handler is unregistered.
     *
     * @param topic   the message topic
     * @param handler the callback handler
     * @return subscriber object returned by the networkservice
     */
    public <K extends Serializable> Subscriber beginCallback(String topic, NetworkMessageListener<K> handler) {
        return this.beginCallback(topic, null, handler);
    }

    /**
     * Registers a callback. Any incoming messages with topic being that message id will be delegated to the handler
     * until the handler is unregistered.
     *
     * @param topic   the message topic
     * @param handler the callback handler
     * @return subscriber object returned by the networkservice
     */
    public Subscriber beginListen(String topic, NetworkMessageListener handler) {
        // create NetworekMessage and Subscriber object
        Subscriber subscriber = new Subscriber();
        subscriber.setPort(this.localPort);
        subscriber.setTopic(topic);
        // subscribe to the message id, and publish the message
        subscriber = this.networkService.save(subscriber);
        this.handlerMap.put(topic, handler);
        return subscriber;
    }

    /**
     * Removes a callbackhandler from the internal {@link #handlerMap} and deletes the subcription.
     *
     * @param subscriber the subscription to remove.
     */
    public void stopSubscription(Subscriber subscriber) {
        this.handlerMap.remove(subscriber.getTopic());
        this.networkService.delete(subscriber.getId());
    }

    /**
     * {@inheritDoc}
     */
    public void publish(NetworkMessage<?> instance) {
        networkService.publish(instance);
    }

}
