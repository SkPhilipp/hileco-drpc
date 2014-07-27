package com.hileco.drpc.core.spec;

import com.hileco.drpc.core.util.SilentCloseable;

/**
 * A more RPC oriented {@link ServiceConnectorHost}.
 *
 * @author Philipp Gayret
 */
public abstract class ServiceHost implements ServiceConnectorHost, MessageSender {

    /**
     * Constructs a topic for an interface without an identifier.
     *
     * @param iface any RPC compliant interface
     * @return the topic string
     */
    public String topic(Class<?> iface) {
        return iface.getName();
    }

    /**
     * Constructs a topic for an interface with an identifier.
     *
     * @param iface      any RPC compliant interface
     * @param identifier the object identifier useable as part of the topic
     * @return the topic string
     */
    public String topic(Class<?> iface, Object identifier) {
        return String.format("%s:%s", iface.getName(), identifier);
    }

    /**
     * Begins listening on the given topic, any messages received on it will be delegated to the given consumer.
     *
     * @param topic    topic to listen on
     * @param consumer handler to accept messages
     * @return the closeable useable to revert the process of this call
     * @throws IllegalArgumentException when there is an open consumer already registered on this topic
     */
    public abstract SilentCloseable bind(String topic, MessageReceiver consumer) throws IllegalArgumentException;

    /**
     * Publishes the given implementation onto the network by listening on its class topic.
     * Only methods delcared on the given iface will be handled.
     *
     * @param iface          any RPC compliant interface
     * @param implementation an implementation of the given interface
     * @param <T>            network object type
     * @return the closeable useable to revert the process of this call
     */
    public abstract <T> SilentCloseable bind(Class<T> iface, T implementation);

}
