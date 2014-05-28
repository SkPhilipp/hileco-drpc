package machine.lib.message.api;

import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

public interface Network {

    /**
     * Begins listening on the given topic, any messages received on it will be delegated to the given typedMessageHandler.
     *
     * @param topic topic to listen on
     * @param typedMessageHandler handler to accept messages
     * @return the given typedMessageHandler
     */
    public Consumer<NetworkMessage> beginListen(String topic, Consumer<NetworkMessage> typedMessageHandler);

    /**
     * Ends listening on the given topic & typedMessageHandler combination, any other consumers listening on this topic
     * will not be affected.
     *
     * @param topic topic to listen on
     * @param typedMessageHandler handler to accept messages
     */
    public void stopListen(String topic, Consumer<NetworkMessage> typedMessageHandler);

    /**
     * Ends listening on the given topic, all listening consumers will no longer receive messages for this topic.
     *
     * @param topic topic to listen on
     */
    public void stopListen(String topic);

    /**
     * Publishes a message onto the network.
     *
     * @param topic the topic of the message
     * @param content the content of the message
     * @param <T> the type of message content
     * @return the message id
     */
    public <T extends Serializable> UUID publishMessage(String topic, T content);

    /**
     * Publishes a predefined message onto the network.
     *
     * @param message the message itself
     * @param <T> the type of message content
     */
    public <T extends Serializable> void publishCustom(NetworkMessage<T> message);

}
