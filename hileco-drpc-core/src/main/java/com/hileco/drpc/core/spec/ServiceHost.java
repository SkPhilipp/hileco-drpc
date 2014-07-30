package com.hileco.drpc.core.spec;

/**
 * A very simple service host, allowing for services to be registered by identifier, and connected to.
 *
 * @author Philipp Gayret
 */
public abstract class ServiceHost implements MessageSender {

    /**
     * Constructs a topic for an interface without an identifier.
     *
     * @param type any RPC compliant interface
     * @return the topic string
     */
    public String topic(Class<?> type) {
        return type.getName();
    }

    /**
     * Publishes the given implementation onto the network by listening on its topic, the topic to listen on is constructed using its identifier.
     * Only methods delcared on the given iface will be handled.
     *
     * @param type           any RPC compliant interface
     * @param identifier     the object identifier useable as part of the topic
     * @param implementation an implementation of the given interface
     * @param <T>            network object type
     * @return the closeable useable to revert the process of this call
     */
    public abstract <T> SilentCloseable registerService(Class<T> type, String identifier, T implementation);

    /**
     * Begins listening on the given callback identifier, any messages received on it will be delegated to the given consumer.
     *
     * @param identifier the object identifier useable as part of the topic
     * @param consumer   handler to accept messages
     * @return the closeable useable to revert the process of this call
     * @throws IllegalArgumentException when there is an open consumer already registered on this topic
     */
    public abstract SilentCloseable registerCallback(String identifier, MessageReceiver consumer) throws IllegalArgumentException;

}
