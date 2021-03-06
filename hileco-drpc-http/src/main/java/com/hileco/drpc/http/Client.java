package com.hileco.drpc.http;

import com.hileco.drpc.core.reflection.ProxyServiceHost;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.spec.SilentCloseable;
import com.hileco.drpc.http.core.HttpClientFactory;
import com.hileco.drpc.http.core.HttpClientMessageSender;
import com.hileco.drpc.http.core.HttpHeaderUtils;
import com.hileco.drpc.http.core.grizzly.GrizzlyServer;
import com.hileco.drpc.http.router.HttpRouter;
import com.hileco.drpc.http.router.services.SubscriptionService;
import com.hileco.drpc.http.subscription.Subscription;
import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.util.UUID;

/**
 * HTTP DRPC module at its highest level.
 *
 * @author Philipp Gayret
 */
public class Client {

    private final ProxyServiceHost proxyServiceHost;
    private final SubscriptionService routerSubscriptionService;
    private final String replyToHost;
    private final Integer port;
    private final GrizzlyServer grizzlyServer;

    /**
     * @param routerUrl   httpclient acceptable url to the router
     * @param replyToHost host to tell the router to reply to
     * @param port        port to listen on, and replyToHost's port to reply to
     * @throws IOException when listening on the given port fails
     */
    public Client(String routerUrl, String replyToHost, Integer port) throws IOException {
        this.replyToHost = replyToHost;
        this.port = port;
        HttpClient httpClient = HttpClientFactory.create();
        HttpClientMessageSender httpClientMessageSender = new HttpClientMessageSender(httpClient, HttpRouter.DEFAULT_STREAMER, routerUrl, replyToHost, port);
        this.proxyServiceHost = new ProxyServiceHost(httpClientMessageSender, HttpRouter.DEFAULT_STREAMER);
        grizzlyServer = new GrizzlyServer();
        grizzlyServer.start(port, (httpRequest) -> {
            Metadata metadata = HttpHeaderUtils.fromHeaders(httpRequest::getHeader);
            proxyServiceHost.accept(metadata, httpRequest.getInputStream());
        });
        this.routerSubscriptionService = this.proxyServiceHost.connector(SubscriptionService.class).connect(HttpRouter.ROUTER_IDENTIFIER);
    }

    /**
     * Publishes a service, informs the router that this client wants to receive messages for the given service.
     *
     * @param type           type to publish, and class' defined methods to allow access to
     * @param identifier     identifier of the implementation
     * @param implementation remote procedure call receiver
     * @param <T>            type of implementation
     * @return closeable to use for unregistering
     */
    public <T> SilentCloseable publish(Class<T> type, String identifier, T implementation) {
        String topic = proxyServiceHost.topic(type);
        Subscription subscription = routerSubscriptionService.save(topic, String.format("http://%s:%d/", replyToHost, port));
        UUID subscriptionId = subscription.getId();
        // TODO: Keep the subscription active via subscriptionservice#extend, as subscriptions will be deleted by the router after inactivity
        SilentCloseable silentCloseable = proxyServiceHost.registerService(type, identifier, implementation);
        return () -> {
            silentCloseable.close();
            routerSubscriptionService.delete(subscriptionId);
        };
    }

    public <T> ServiceConnector<T> connector(Class<T> type) {
        return proxyServiceHost.connector(type);
    }

    public void shutdownNow() {
        this.grizzlyServer.shutdownNow();
    }

}
