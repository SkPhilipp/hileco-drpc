package machine.drcp.core.api;

import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.util.SilentCloseable;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * A basic client to the DRCP router, for "low level" communication.
 */
public interface MessageClient {

    /**
     * Begins listening on the given topic, any messages received on it will be delegated to the given consumer.
     *
     * @param topic    topic to listen on
     * @param consumer handler to accept messages
     * @return the closeable useable to revert the process of this call
     */
    public SilentCloseable listen(String topic, Consumer<Message<?>> consumer);

    /**
     * Publishes a message onto the network. If the message does not have an id assigned, this will assign a random id.
     *
     * @param message the message to be published
     * @return the message identifier used for publishing
     */
    public UUID publish(Message<?> message);

}
