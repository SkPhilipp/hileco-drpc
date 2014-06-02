package machine.drcp.core.routing;

import machine.drcp.core.api.Connector;
import machine.drcp.core.api.ObjectClient;
import machine.drcp.core.routing.proxy.ProxyConnector;
import machine.drcp.core.routing.proxy.RPCMessageConsumer;
import machine.drcp.core.routing.proxy.ObjectConverter;
import machine.drcp.core.routing.services.RouterService;

public class ProxyObjectClient extends ForwardingMessageClient implements ObjectClient {

    private static final ObjectConverter OBJECT_CONVERTER = new JSONObjectConverter();

    /**
     * @param localPort     port over which this service is made to be available
     * @param routerService networkservice on which to subscribe and publish messages over
     */
    public ProxyObjectClient(Integer localPort, RouterService routerService) {
        super(localPort, routerService);
    }

    @Override
    public <T, P> Connector<T, P> connector(Class<T> iface) {
        return new ProxyConnector<>(this, iface, OBJECT_CONVERTER);
    }

    @Override
    public <T, P> AutoCloseable listen(Class<T> iface, T implementation, P identifier) {
        String topic = this.topic(iface, identifier);
        RPCMessageConsumer rpcMessageConsumer = new RPCMessageConsumer(this, implementation, OBJECT_CONVERTER);
        return this.listen(topic, rpcMessageConsumer);
    }

}
