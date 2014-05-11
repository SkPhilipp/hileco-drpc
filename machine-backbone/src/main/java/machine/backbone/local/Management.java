package machine.backbone.local;

import machine.backbone.util.proxy.Resolver;
import machine.backbone.util.proxy.ResolvingProxyFactory;
import machine.management.api.services.NetworkService;
import machine.management.api.services.ServerService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import java.util.Collections;
import java.util.List;

/**
 * Holds all external services.
 *
 * Getters return proxies to actual service implementations; This is so that we can change the URL with CXF.
 */
public class Management {

    private static final ResolvingProxyFactory PROXY_FACTORY = new ResolvingProxyFactory();
    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());

    private final ServerService proxyServerService;
    private final NetworkService proxyNetworkService;

    private ServerService actualServerService;
    private NetworkService actualNetworkService;

    /**
     * Instantiates a Management object, creates all service proxies.
     *
     * @param managementURL full url to managenent, i.e. "http://localhost:80/"
     */
    public Management(String managementURL) {
        this.proxyServerService = PROXY_FACTORY.create(ServerService.class, new Resolver<ServerService>() {
            @Override
            public ServerService resolve() {
                return Management.this.actualServerService;
            }
        });
        this.proxyNetworkService = PROXY_FACTORY.create(NetworkService.class, new Resolver<NetworkService>() {
            @Override
            public NetworkService resolve() {
                return Management.this.actualNetworkService;
            }
        });
        this.setURL(managementURL);
    }

    /**
     * Initializes all client proxies, point them to the given management URL.
     *
     * @param managementURL where all the services are.
     */
    public void setURL(String managementURL) {
        actualServerService = JAXRSClientFactory.create(managementURL, ServerService.class, PROVIDERS);
        actualNetworkService = JAXRSClientFactory.create(managementURL, NetworkService.class, PROVIDERS);
    }

    public ServerService getServerService() {
        return proxyServerService;
    }

    public NetworkService getNetworkService() {
        return proxyNetworkService;
    }

}
