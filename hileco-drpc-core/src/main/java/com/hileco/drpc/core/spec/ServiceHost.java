package com.hileco.drpc.core.spec;

import com.hileco.drpc.core.util.SilentCloseable;

/**
 * RPC client speficication.
 *
 * @author Philipp Gayret
 */
public interface ServiceHost extends OutgoingMessageConsumer {

    /**
     * Constructs a topic for an interface without an identifier.
     *
     * @param iface any RPC compliant interface
     * @return the topic string
     */
    public String topic(Class<?> iface);

    /**
     * Constructs a topic for an interface with an identifier.
     *
     * @param iface      any RPC compliant interface
     * @param identifier the object identifier useable as part of the topic
     * @return the topic string
     */
    public String topic(Class<?> iface, Object identifier);

    /**
     * Begins listening on the given topic, any messages received on it will be delegated to the given consumer.
     *
     * @param topic    topic to listen on
     * @param consumer handler to accept messages
     * @return the closeable useable to revert the process of this call
     * @throws IllegalArgumentException when there is an open consumer already registered on this topic
     */
    public SilentCloseable bind(String topic, IncomingMessageConsumer consumer) throws IllegalArgumentException;

    /**
     * Publishes the given implementation onto the network by listening on its topic, the topic to listen on is constructed using its identifier.
     * Only methods delcared on the given iface will be handled.
     *
     * @param iface          any RPC compliant interface
     * @param implementation an implementation of the given interface
     * @param identifier     the object identifier useable as part of the topic
     * @param <T>            network object type
     * @return the closeable useable to revert the process of this call
     */
    public <T> SilentCloseable bind(Class<T> iface, T implementation, String identifier);

    /**
     * Publishes the given implementation onto the network by listening on its class topic.
     * Only methods delcared on the given iface will be handled.
     *
     * @param iface          any RPC compliant interface
     * @param implementation an implementation of the given interface
     * @param <T>            network object type
     * @return the closeable useable to revert the process of this call
     */
    public <T> SilentCloseable bind(Class<T> iface, T implementation);

    /**
     * Constructs a {@link ServiceConnector} out of the given interface class, useable for RPC and DRPC.
     *
     * @param iface any RPC compliant interface
     * @param <T>   network object type
     * @return the given class' {@link ServiceConnector}
     */
    public <T> ServiceConnector<T> connector(Class<T> iface);

}
