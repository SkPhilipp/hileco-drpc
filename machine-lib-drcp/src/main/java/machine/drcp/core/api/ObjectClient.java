package machine.drcp.core.api;

/**
 * An object-oriented client to the DRCP router.
 */
public interface ObjectClient extends MessageClient {

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
    public <T, P> AutoCloseable listen(Class<T> iface, T implementation, P identifier);

}
