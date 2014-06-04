package machine.drcp.core.api;

import machine.drcp.core.api.util.SilentCloseable;

/**
 * An object-oriented client to the DRCP router, providing low level messages and .
 */
public interface Client extends MessageClient {

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
     * Constructs a {@link Connector} out of the given interface class, useable RPC and DRPC.
     *
     * @param iface any RPC compliant interface
     * @param <T>   network object type
     * @param <P>   network object identifier type
     * @return the given class' {@link Connector}
     */
    public <T, P> Connector<T, P> connector(Class<T> iface);

    /**
     * Publishes the given implementation onto the network by listening on its topic, the topic to listen on is constructed using its identifier.
     * Only methods delcared on the given iface will be handled.
     *
     * @param iface          any RPC compliant interface
     * @param implementation an implementation of the given interface
     * @param identifier      the object identifier useable as part of the topic
     * @param <T>            network object type
     * @param <P>            network object identifier type
     * @return the closeable useable to revert the process of this call
     */
    public <T, P> SilentCloseable listen(Class<T> iface, T implementation, P identifier);

    /**
     * Publishes the given implementation onto the network by listening on its class topic.
     * Only methods delcared on the given iface will be handled.
     *
     * @param iface          any RPC compliant interface
     * @param implementation an implementation of the given interface
     * @param <T>            network object type
     * @return the closeable useable to revert the process of this call
     */
    public <T> SilentCloseable listen(Class<T> iface, T implementation);

}
