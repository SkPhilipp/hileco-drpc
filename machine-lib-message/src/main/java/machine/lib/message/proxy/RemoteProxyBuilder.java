package machine.lib.message.proxy;

import machine.lib.message.api.Network;
import machine.lib.message.api.NetworkConnector;

import java.lang.reflect.Proxy;
import java.util.function.Function;

public class RemoteProxyBuilder implements NetworkConnector {

    private ClassLoader classLoader;
    private Network network;

    public RemoteProxyBuilder(Network network) {
        this.network = network;
        this.classLoader = this.getClass().getClassLoader();
    }

    @SuppressWarnings("unchecked")
    public <P, T extends BoundRemote<P>> Function<P, T> remoteBound(Class<T> proxyable) {
        return binding -> {
            RemotedInvocationHandler invocationHandler = new RemotedInvocationHandler(network, proxyable, binding);
            return (T) Proxy.newProxyInstance(classLoader, new Class[]{proxyable}, invocationHandler);
        };
    }

    @SuppressWarnings("unchecked")
    public <T extends Remote> T remote(Class<T> proxyable) {
        RemotedInvocationHandler invocationHandler = new RemotedInvocationHandler(network, proxyable);
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{proxyable}, invocationHandler);
    }

    public <T extends Remote> void listen(Class<T> iface, Remote remote) {
        this.network.beginListen(RemoteTopics.getTopic(iface), new RemotedListener(remote));
    }

    public <T extends Remote, P> void listen(Class<T> iface, BoundRemote<P> remote, P binding) {
        this.network.beginListen(RemoteTopics.getTopic(iface, binding), new RemotedListener(remote));
    }

    public <T extends Remote> void endListen(Class<T> iface, Remote remote) {
        // TODO: implement
    }

    public <T extends Remote, P> void endListen(Class<T> iface, BoundRemote<P> remote, P binding) {
        // TODO: implement
    }


}
