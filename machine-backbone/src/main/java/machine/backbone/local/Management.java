package machine.backbone.local;

import machine.backbone.util.proxy.Resolver;
import machine.backbone.util.proxy.ResolvingProxyFactory;
import machine.management.api.receiver.services.MessageService;
import machine.management.api.services.*;
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

    private final MessageService proxyMessageService;
    private final ServerService proxyServerService;
    private final SubscriberService proxySubscriberService;

    private MessageService actualMessageService;
    private ServerService actualServerService;
    private SubscriberService actualSubscriberService;

    /**
     * Instantiates a Management object, creates all service proxies.
     *
     * @param managementURL full url to managenent, i.e. "http://localhost:80/"
     */
    public Management(String managementURL) {
        this.proxyMessageService = PROXY_FACTORY.create(MessageService.class, new Resolver<MessageService>() {
            @Override
            public MessageService resolve() {
                return Management.this.actualMessageService;
            }
        });
        this.proxyServerService = PROXY_FACTORY.create(ServerService.class, new Resolver<ServerService>() {
            @Override
            public ServerService resolve() {
                return Management.this.actualServerService;
            }
        });
        this.proxySubscriberService = PROXY_FACTORY.create(SubscriberService.class, new Resolver<SubscriberService>() {
            @Override
            public SubscriberService resolve() {
                return Management.this.actualSubscriberService;
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
        actualMessageService = JAXRSClientFactory.create(managementURL, MessageService.class, PROVIDERS);
        actualServerService = JAXRSClientFactory.create(managementURL, ServerService.class, PROVIDERS);
        actualSubscriberService = JAXRSClientFactory.create(managementURL, SubscriberService.class, PROVIDERS);
    }

    public MessageService getMessageService() {
        return proxyMessageService;
    }

    public ServerService getServerService() {
        return proxyServerService;
    }

    public SubscriberService getSubscriberService() {
        return proxySubscriberService;
    }

}
