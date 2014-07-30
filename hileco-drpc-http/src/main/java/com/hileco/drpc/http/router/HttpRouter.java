package com.hileco.drpc.http.router;

import com.google.common.io.ByteStreams;
import com.hileco.drpc.core.ProxyServiceHost;
import com.hileco.drpc.core.spec.MessageSender;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.spec.ServiceHost;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.stream.JSONArgumentsStreamer;
import com.hileco.drpc.http.core.HttpClientFactory;
import com.hileco.drpc.http.core.HttpStreamedEntity;
import com.hileco.drpc.http.core.HttpHeaderUtils;
import com.hileco.drpc.http.router.services.Subscription;
import com.hileco.drpc.http.router.services.SubscriptionStore;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Philipp Gayret
 */
public class HttpRouter implements MessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRouter.class);

    public static final String ROUTER_IDENTIFIER = "ROUTER";
    public static final int DEFAULT_SENDER_POOL_SIZE = 100;
    public static final ArgumentsStreamer DEFAULT_STREAMER = new JSONArgumentsStreamer();

    private final ProxyServiceHost proxyServiceHost;
    private final SubscriptionStore subscriptionStore;
    private final HttpClient httpClient;
    private final ScheduledExecutorService executorService;
    private final ArgumentsStreamer argumentsStreamer;

    /**
     * @param subscriptionStore store to obtain subscription lists lists from for given messages' topics
     */
    public HttpRouter(SubscriptionStore subscriptionStore) {
        this.argumentsStreamer = HttpRouter.DEFAULT_STREAMER;
        this.proxyServiceHost = new ProxyServiceHost(this, argumentsStreamer);
        this.subscriptionStore = subscriptionStore;
        this.executorService = Executors.newScheduledThreadPool(DEFAULT_SENDER_POOL_SIZE);
        this.httpClient = HttpClientFactory.create();
    }

    private void send(Metadata metadata, HttpEntity httpEntity) {
        Collection<Subscription> subscriptions = this.subscriptionStore.withTopic(metadata.getTopic());
        for (Subscription subscription : subscriptions) {
            this.executorService.submit(() -> {
                try {
                    String target = String.format("http://%s:%d/", subscription.getHost(), subscription.getPort());
                    HttpPost request = new HttpPost(target);
                    HttpHeaderUtils.writeHeaders(metadata, request::setHeader);
                    request.setEntity(httpEntity);
                    HttpResponse response = httpClient.execute(request);
                    int statusCode = response.getStatusLine().getStatusCode();
                    response.getEntity().getContent().close();
                    if (statusCode == 400) {
                        LOG.debug("Request completed against {}, status code indicates subscription {} must be deleted.", target, subscription.getId());
                        this.subscriptionStore.delete(subscription.getId());
                    } else {
                        LOG.debug("Sent message with topic {} subscription {} to {}.", metadata.getTopic(), subscription.getId(), target);
                    }
                } catch (Throwable e) {
                    LOG.warn("Erred while sending to a subscribed receiver", e);
                }
            });
        }
    }

    @Override
    public void send(Metadata metadata, Object[] content) {
        HttpStreamedEntity httpStreamedEntity = new HttpStreamedEntity(this.argumentsStreamer, content);
        this.send(metadata, httpStreamedEntity);
    }

    /**
     * Accepts a message and asynchronously forwards it to all subscribers known for the metadata's topic.
     *
     * @param replyToHost host listening for replies
     * @param replyToPort replyToHost's port listening for replies
     * @param metadata    metadata describing how to handle the content
     * @param content     stream to content to process
     * @throws IOException when reading fails
     */
    public void accept(String replyToHost, Integer replyToPort, Metadata metadata, InputStream content) throws IOException {

        // if the client expects a response, save a subscription
        if (metadata.getExpectResponse()) {
            Subscription subscription = new Subscription();
            subscription.setHost(replyToHost);
            subscription.setPort(replyToPort);
            subscription.setId(UUID.randomUUID());
            subscription.setTopic(metadata.getId());
            this.subscriptionStore.save(subscription);
        }

        // if the client is targeting the router, handle accordingly
        if (metadata.hasTargets() && metadata.getTargets().contains(ROUTER_IDENTIFIER)) {
            LOG.debug("Internally handling a message of topic {} and id {}", metadata.getTopic(), metadata.getId());
            proxyServiceHost.accept(metadata, content);
        }

        // if the client is not targeting the router, publish it to all subscribers to the topic
        else {
            LOG.debug("Handling a message of topic {} and id {}", metadata.getTopic(), metadata.getId());
            byte[] bytes = ByteStreams.toByteArray(content);
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
            this.send(metadata, byteArrayEntity);
        }

    }

    /**
     * @return service host listening only on {@link #ROUTER_IDENTIFIER} identifier referenced metadata messages.
     */
    public ServiceHost getRouterServiceHost() {
        return proxyServiceHost;
    }

}
