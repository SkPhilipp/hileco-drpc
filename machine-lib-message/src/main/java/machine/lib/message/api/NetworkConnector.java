package machine.lib.message.api;

import java.util.function.Function;

public interface NetworkConnector {

    public <P, T extends NetworkObject<P>> Function<P, T> remoteBound(Class<T> proxyable);

    public <T extends NetworkService> T remote(Class<T> proxyable);

    public <T extends NetworkService> void listen(Class<T> iface, NetworkService networkService);

    public <T extends NetworkObject, P> void listen(Class<T> iface, NetworkObject<P> remote, P binding);

    public <T extends NetworkService> void endListen(Class<T> iface, NetworkService networkService);

    public <T extends NetworkObject, P> void endListen(Class<T> iface, NetworkObject<P> remote, P binding);

}
