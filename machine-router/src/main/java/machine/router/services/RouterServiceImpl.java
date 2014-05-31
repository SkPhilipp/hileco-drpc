package machine.router.services;

import com.google.common.base.Preconditions;
import machine.router.dao.SubscriptionDAO;
import machine.router.api.entities.NetworkMessage;
import machine.router.api.entities.Subscription;
import machine.router.api.services.MessageService;
import machine.router.api.services.RouterService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RouterServiceImpl implements RouterService {

    public static final int DEFAULT_REQUEST_TIMEOUT = 2000;
    public static final int DEFAULT_SENDER_POOL_SIZE = 100;
    private static final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(RouterServiceImpl.class);
    private final HttpClient httpClient;
    private final ScheduledExecutorService executorService;
    @Context
    private HttpServletRequest request;

    public RouterServiceImpl() {
        this.executorService = Executors.newScheduledThreadPool(DEFAULT_SENDER_POOL_SIZE);
        RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT)
                .setConnectTimeout(DEFAULT_REQUEST_TIMEOUT)
                .setSocketTimeout(DEFAULT_REQUEST_TIMEOUT)
                .build();
        this.httpClient = HttpClients.custom()
                .setMaxConnTotal(DEFAULT_SENDER_POOL_SIZE)
                .setDefaultRequestConfig(config)
                .build();

    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Finds all targets subcribed to the given topic.
     *
     * @param topic an event topic
     * @return all targets subcribed to the given topic
     */
    public Map<UUID, String> getTargets(String topic) {
        Subscription example = new Subscription();
        example.setTopic(topic);
        Map<UUID, String> targets = new HashMap<>();
        for (Subscription subscription : subscriptionDAO.query(example)) {
            String target = String.format("http://%s:%d/message/?%s=%s", subscription.getHost(), subscription.getPort(), MessageService.SUBSCRIPTION_ID, subscription.getId().toString());
            targets.put(subscription.getId(), target);
        }
        return targets;
    }

    /**
     * Sends a message for each subscription to the message's topic.
     *
     * @param networkMessage {@link NetworkMessage} message to distribute
     */
    @Override
    public void publish(NetworkMessage networkMessage) {
        Preconditions.checkArgument(networkMessage.getTopic() != null, "Topic must not be empty");
        Map<UUID, String> targets = this.getTargets(networkMessage.getTopic());
        try {
            String body = OBJECT_MAPPER.writeValueAsString(networkMessage);
            final StringEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
            for (Map.Entry<UUID, String> entry : targets.entrySet()) {
                final String target = entry.getValue();
                final UUID subscriptionId = entry.getKey();
                this.executorService.submit(() -> {
                    HttpPost request = new HttpPost(target);
                    request.setEntity(stringEntity);
                    try {
                        HttpResponse response = httpClient.execute(request);
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == 400) {
                            LOG.debug("Request completed against {}, status code indicates subscription {} must be deleted.", target, subscriptionId);
                            RouterServiceImpl.this.delete(subscriptionId);
                        } else {
                            LOG.debug("Sent message with topic {} subscription {} to {}.", networkMessage.getTopic(), subscriptionId, target);
                        }
                    } catch (ConnectException e) {
                        LOG.warn("Unable to connect to subscribed receiver {}", target);
                    } catch (Throwable e) {
                        LOG.warn("Erred while sending to a subscribed receiver", e);
                    }
                });
            }
        } catch (IOException e) {
            LOG.error("Unable to serialize a networkmessage, with id {} and topic {}", networkMessage.getMessageId(), networkMessage.getTopic());
        }
    }

    @Override
    public Subscription read(UUID id) {
        return subscriptionDAO.read(id);
    }

    @Override
    public void delete(UUID id) {
        subscriptionDAO.delete(id);
    }

    @Override
    public void extend(UUID id) {
        Subscription subscription = this.read(id);
        if (subscription != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, DEFAULT_SUBSCRIPTION_EXPIRE_TIME);
            Date date = calendar.getTime();
            subscription.setExpires(date);
            this.save(subscription);
        } else {
            LOG.warn("An attempt was made by a client to extend a subscription with id {}, but no subscription was found for it.", id);
        }
    }

    @Override
    public Subscription save(Subscription instance) {
        Preconditions.checkArgument(instance.getHost() == null, "Clients are not permitted to provide the IP-address themselves.");
        Preconditions.checkNotNull(instance.getTopic(), "Clients must provide a topic.");
        Preconditions.checkNotNull(instance.getPort(), "Clients must provide a port.");
        String clientIpAddress = this.request.getRemoteAddr();
        instance.setHost(clientIpAddress);
        if (instance.getExpires() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, DEFAULT_SUBSCRIPTION_EXPIRE_TIME);
            Date date = calendar.getTime();
            instance.setExpires(date);
        }
        return subscriptionDAO.save(instance);
    }

}
