package com.hileco.drpc.http.routing;

import com.google.common.io.ByteStreams;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.HeaderUtils;
import com.hileco.drpc.http.servlet.HttpConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
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
public class Router {

    private static final Logger LOG = LoggerFactory.getLogger(Router.class);

    public static final int DEFAULT_SENDER_POOL_SIZE = 100;
    public static final int DEFAULT_REQUEST_TIMEOUT = 2000;

    private final SubscriptionStore subscriptionStore;
    private final HttpClient httpClient;
    private final ScheduledExecutorService executorService;

    public Router(SubscriptionStore subscriptionStore) {
        this.subscriptionStore = subscriptionStore;
        this.executorService = Executors.newScheduledThreadPool(DEFAULT_SENDER_POOL_SIZE);
        RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT)
                .setConnectTimeout(DEFAULT_REQUEST_TIMEOUT)
                .setSocketTimeout(DEFAULT_REQUEST_TIMEOUT)
                .build();
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
    }

    public Router() {
        this(new CacheSubscriptionStore());
    }

    public void extend(UUID id) {
        Subscription subscription = subscriptionStore.read(id);
        if (subscription != null) {
            this.subscriptionStore.save(subscription);
        } else {
            LOG.warn("An attempt was made by a client to extend a subscription with id {}, but no subscription was found for it.", id);
        }
    }

    public Subscription save(String topic, String host, Integer port) {
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);
        subscription.setPort(port);
        subscription.setHost(host);
        subscription.setId(UUID.randomUUID());
        return subscriptionStore.save(subscription);
    }

    public void delete(UUID id) {
        this.subscriptionStore.delete(id);
    }

    /**
     * Accepts a message and asynchronously forwards it to all subscribers known for the metadata's topic.
     *
     * @param metadata metadata describing how to handle the content
     * @param content  stream to content to process
     * @throws IOException when reading fails
     */
    public void accept(String remoteHost, Integer remotePort, Metadata metadata, InputStream content) throws IOException {

        LOG.debug("Publishing a message of topic {} and id {}", metadata.getTopic(), metadata.getId());
        Collection<Subscription> subscriptions = subscriptionStore.withTopic(metadata.getTopic());
        byte[] bytes = ByteStreams.toByteArray(content);
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);

        for (Subscription httpSubscription : subscriptions) {
            this.executorService.submit(() -> {
                try {
                    String target = String.format("http://%s:%d/%s", httpSubscription.getHost(), httpSubscription.getPort(), HttpConstants.HDRPC_CONSUMER_PATH);
                    HttpPost request = new HttpPost(target);
                    HeaderUtils.writeHeaders(metadata, request::setHeader);
                    request.setEntity(byteArrayEntity);
                    HttpResponse response = httpClient.execute(request);
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 400) {
                        LOG.debug("Request completed against {}, status code indicates subscription {} must be deleted.", target, httpSubscription.getId());
                        this.delete(httpSubscription.getId());
                    } else {
                        LOG.debug("Sent message with topic {} subscription {} to {}.", metadata.getTopic(), httpSubscription.getId(), target);
                    }
                } catch (Throwable e) {
                    LOG.warn("Erred while sending to a subscribed receiver", e);
                }
            });
        }

    }

}
