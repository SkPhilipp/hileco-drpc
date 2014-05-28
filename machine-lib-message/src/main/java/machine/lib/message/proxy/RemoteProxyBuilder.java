package machine.lib.message.proxy;

import machine.lib.message.api.Network;
import machine.lib.message.api.NetworkConnector;
import machine.lib.message.api.NetworkObject;
import machine.lib.message.api.NetworkService;

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
    public <P, T extends NetworkObject<P>> Function<P, T> remoteObject(Class<T> proxyable) {
        return binding -> {
            RemotedInvocationHandler invocationHandler = new RemotedInvocationHandler(network, proxyable, binding);
            return (T) Proxy.newProxyInstance(classLoader, new Class[]{proxyable}, invocationHandler);
        };
    }

    @SuppressWarnings("unchecked")
    public <T extends NetworkService> T remoteService(Class<T> proxyable) {
        RemotedInvocationHandler invocationHandler = new RemotedInvocationHandler(network, proxyable);
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{proxyable}, invocationHandler);
    }

    public <T extends NetworkService> void listen(Class<T> iface, T networkService) {
        this.network.beginListen(RemoteTopics.getTopic(iface), new RemotedListener(networkService));
    }

    public <T extends NetworkObject<P>, P> void listen(Class<T> iface, T networkObject, P binding) {
        this.network.beginListen(RemoteTopics.getTopic(iface, binding), new RemotedListener(networkObject));
    }

    public <T extends NetworkService> void endListen(Class<T> iface, NetworkService networkService) {
        // TODO: implement
    }

    public <T extends NetworkObject, P> void endListen(Class<T> iface, NetworkObject<P> networkObject, P binding) {
        // TODO: implement
    }

    @Override
    public Network getNetwork() {
        return network;
    }

}
