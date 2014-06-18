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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RouterClient implements JaxrsMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(RouterClient.class);

    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectConverter OBJECT_CONVERTER = OBJECT_MAPPER::convertValue;
    private final ProxyClient proxyClient;

    public RouterClient(String routerBaseAddress, Supplier<HTTPSubscription> localAddressSubscriptionSupplier) {
        RouterService routerService = JAXRSClientFactory.create(routerBaseAddress, JaxrsRouterService.class, PROVIDERS);
        this.proxyClient = new ProxyClient(localAddressSubscriptionSupplier, routerService, OBJECT_CONVERTER);
    }

    public RouterClient(RouterService routerService, Supplier<HTTPSubscription> localAddressSubscriptionSupplier) {
        this.proxyClient = new ProxyClient(localAddressSubscriptionSupplier, routerService, OBJECT_CONVERTER);
    }

    @Override
    public void handle(UUID subscriptionId, Message<?> instance) throws NotSubscribedException {
        LOG.trace("Received message of topic {} and id {} for subscription {}", instance.getTopic(), instance.getId(), subscriptionId);
        this.proxyClient.handle(subscriptionId, instance);
    }

    public ProxyClient getClient() {
        return this.proxyClient;
    }

}
