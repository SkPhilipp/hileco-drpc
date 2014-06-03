package machine.drcp.http.impl;

import com.google.common.base.Supplier;
import machine.drcp.core.api.exceptions.NotSubscribedException;
import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.services.RouterService;
import machine.drcp.core.client.ObjectConverter;
import machine.drcp.core.client.ProxyClient;
import machine.drcp.http.api.JaxrsMessageService;
import machine.drcp.http.api.JaxrsRouterService;
import machine.drcp.http.api.models.HTTPSubscription;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RouterClient implements JaxrsMessageService {

    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectConverter OBJECT_CONVERTER = OBJECT_MAPPER::convertValue;
    private final ProxyClient proxyClient;

    public RouterClient(Supplier<HTTPSubscription> localAddressSubscriptionSupplier, String routerBaseAddress) {
        RouterService routerService = JAXRSClientFactory.create(routerBaseAddress, JaxrsRouterService.class, PROVIDERS);
        this.proxyClient = new ProxyClient(localAddressSubscriptionSupplier, routerService, OBJECT_CONVERTER);
    }

    @Override
    public void handle(UUID subscriptionId, Message<?> instance) throws NotSubscribedException {
        this.proxyClient.handle(subscriptionId, instance);
    }

    public ProxyClient getProxyClient() {
        return this.proxyClient;
    }

}
