package machine.lib.message.api;

import java.util.function.Function;

/**
 * Connects {@link NetworkObject}s and {@link NetworkService}s to a {@link Network}.
 */
public interface NetworkConnector {

    /**
     * Constructs a function that consumes network object identifiers and returns a {@link java.lang.reflect.Proxy}'d
     * {@link NetworkObject} using the given identifier.
     *
     * Any method calls on the proxy will transformed into messages and published onto a {@link Network}.
     * These messages are one-way only; the proxy will always return null on method invocations.
     *
     * @param proxyable any network object's class
     * @param <P> network object identifier type
     * @param <T> network object type
     * @return a function when given an identifier of type {@link P} returns the
     */
    public <P, T extends NetworkObject<P>> Function<P, T> remoteObject(Class<T> proxyable);

    /**
     * Constructs {@link java.lang.reflect.Proxy}'d {@link NetworkService}.
     *
     * Any method calls on the proxy will transformed into messages and published onto a {@link Network}.
     * These messages are one-way only; the proxy will always return null on method invocations.
     *
     * @param proxyable any network service's class
     * @param <T> network service type
     * @return a {@link java.lang.reflect.Proxy}
     */
    public <T extends NetworkService> T remoteService(Class<T> proxyable);

    /**
     * Publishes the given networkService onto the network by listening on its topic. Any methods on the given
     * iface will be handled when invocation messages are received.
     *
     * @param iface the interface containing methods to handle
     * @param networkService an implementation of the given interface
     * @param <T> the networkservice interface type
     */
    public <T extends NetworkService> void listen(Class<T> iface, T networkService);


    /**
     * Publishes the given networkObject onto the network by listening on its topic, the topic to listen on is
     * constructed using its binding. Any methods on the given iface will be handled when invocation messages are received.
     *
     * @param iface the interface containing methods to handle
     * @param networkObject an implementation of the given interface
     * @param binding the object identifier useable as part of the topic
     * @param <T> network object type
     * @param <P> network object identifier type
     */
    public <T extends NetworkObject<P>, P> void listen(Class<T> iface, T networkObject, P binding);

    /**
     * Ends listening for all iface-defined method invocation messages on the given networkService.
     *
     * @param iface the interface containing methods to handle
     * @param networkService an implementation of the given interface
     * @param <T> the networkservice interface type
     */
    public <T extends NetworkService> void endListen(Class<T> iface, NetworkService networkService);

    /**
     * Ends listening for all iface-defined method invocation messages on the given networkObject and binding combination.
     *
     * @param iface the interface containing methods to handle
     * @param networkObject an implementation of the given interface
     * @param binding the object identifier useable as part of the topic
     * @param <T> network object type
     * @param <P> network object identifier type
     */
    public <T extends NetworkObject, P> void endListen(Class<T> iface, NetworkObject<P> networkObject, P binding);

    /**
     * @return the network this connector is active on
     */
    public Network getNetwork();

}
