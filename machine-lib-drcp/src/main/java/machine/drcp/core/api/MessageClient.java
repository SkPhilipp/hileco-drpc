package machine.drcp.core.api;

import machine.drcp.core.api.entities.Message;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * A message-oriented client to the DRCP router.
 */
public interface MessageClient {

    /**
     * Constructs a topic for an interface without an identifier.
     *
     * @param iface any RPC compliant interface
     * @param <T>   network object type
     * @return the topic string
     */
    default <T> String topic(Class<T> iface) {
        return iface.getName();
    }

    /**
     * Constructs a topic for an interface with an identifier.
     *
     * @param iface      any RPC compliant interface
     * @param identifier the object identifier useable as part of the topic
     * @param <T>        network object type
     * @param <P>        network object identifier type
     * @return the topic string
     */
    default <T, P> String topic(Class<T> iface, P identifier) {
        return String.format("%s:%s", iface.getName(), identifier);
    }

    /**
     * Begins listening on the given topic, any messages received on it will be delegated to the given consumer.
     *
     * @param topic    topic to listen on
     * @param consumer handler to accept messages
     * @return the closeable useable to revert the process of this call
     */
    public AutoCloseable listen(String topic, Consumer<Message<?>> consumer);

    /**
     * Publishes a message onto the network. If the message does not have an id assigned, this will assign a random id.
     *
     * @param message the message to be published
     * @return the message identifier used for publishing
     */
    public UUID publish(Message<?> message);

}
