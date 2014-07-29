package com.hileco.drpc.http;

import com.hileco.drpc.core.ProxyServiceHost;
import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.util.SilentCloseable;
import com.hileco.drpc.http.core.HttpMessageSender;
import com.hileco.drpc.http.routing.HttpRouter;
import com.hileco.drpc.http.routing.services.SubscriptionService;
import com.hileco.drpc.http.server.MessageReceiverServer;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * HTTP DRPC module at its highest level.
 *
 * @author Philipp Gayret
 */
public class Client {

    private final ProxyServiceHost proxyServiceHost;
    private final SubscriptionService routerSubscriptionService;
    private String replyToHost;
    private Integer port;

    /**
     * @param routerUrl   httpclient acceptable url to the router
     * @param replyToHost host to tell the router to reply to
     * @param port        port to listen on, and replyToHost's port to reply to
     * @throws IOException when listening on the given port fails
     */
    public Client(String routerUrl, String replyToHost, Integer port) throws IOException {
        this.replyToHost = replyToHost;
        this.port = port;
        HttpClient httpClient = HttpClients.createDefault();
        HttpMessageSender httpMessageSender = new HttpMessageSender(httpClient, HttpRouter.DEFAULT_STREAMER, routerUrl, replyToHost, port);
        this.proxyServiceHost = new ProxyServiceHost(httpMessageSender, HttpRouter.DEFAULT_STREAMER);
        MessageReceiverServer messageReceiverServer = new MessageReceiverServer();
        messageReceiverServer.start(port, proxyServiceHost);
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
        routerSubscriptionService.save(topic, replyToHost, port);
        // TODO: Keep the subscription active, and delete via silentcloseable wrapper on close
        return proxyServiceHost.registerService(type, identifier, implementation);
    }

    public <T> ServiceConnector<T> connector(Class<T> type) {
        return proxyServiceHost.connector(type);
    }

}
