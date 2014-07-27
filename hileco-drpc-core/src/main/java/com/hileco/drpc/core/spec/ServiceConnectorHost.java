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
     * @param iface any RPC compliant interface
     * @param <T>   network object type
     * @return the given class' {@link ServiceConnector}
     */
    public abstract <T> ServiceConnector<T> connector(Class<T> iface);

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
    public abstract <T> SilentCloseable bind(Class<T> iface, T implementation, String identifier);

}
