package machine.lib.message.api;

import machine.lib.message.proxy.BoundRemote;
import machine.lib.message.proxy.Remote;

import java.util.function.Function;

public interface NetworkConnector {

    public <P, T extends BoundRemote<P>> Function<P, T> remoteBound(Class<T> proxyable);

    public <T extends Remote> T remote(Class<T> proxyable);

    public <T extends Remote> void listen(Class<T> iface, Remote remote);

    public <T extends Remote, P> void listen(Class<T> iface, BoundRemote<P> remote, P binding);

    public <T extends Remote> void endListen(Class<T> iface, Remote remote);

    public <T extends Remote, P> void endListen(Class<T> iface, BoundRemote<P> remote, P binding);

}
