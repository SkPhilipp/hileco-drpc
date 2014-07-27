package com.hileco.drpc.core.spec;

import com.hileco.drpc.core.util.SilentCloseable;

/**
 * A very simple service host, allowing for services to be registered by identifier, and connected to.
 *
 * @author Philipp Gayret
 */
public interface ServiceConnectorHost {

    /**
     * Constructs a {@link ServiceConnector} out of the given interface class, useable for RPC and DRPC.
     *
     * @param type any RPC compliant interface
     * @param <T>  network object type
     * @return the given class' {@link ServiceConnector}
     */
    public <T> ServiceConnector<T> connector(Class<T> type);

    /**
     * Constructs a topic for an interface without an identifier.
     *
     * @param type any RPC compliant interface
     * @return the topic string
     */
    public default String topic(Class<?> type) {
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
    public <T> SilentCloseable registerService(Class<T> type, String identifier, T implementation);

}
