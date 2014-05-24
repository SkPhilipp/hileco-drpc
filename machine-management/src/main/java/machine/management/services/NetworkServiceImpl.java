package machine.management.services;

import com.google.common.base.Preconditions;
import machine.management.api.entities.Subscription;
import machine.management.api.services.NetworkService;
import machine.management.dao.GenericModelDAO;
import machine.message.api.entities.NetworkMessage;
import machine.message.api.services.MessageService;
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
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NetworkServiceImpl extends AbstractQueryableModelService<Subscription> implements NetworkService {

    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    public static final int DEFAULT_SENDER_POOL_SIZE = 100;
    private static final GenericModelDAO<Subscription> subscriptionDAO = new GenericModelDAO<>(Subscription.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(NetworkServiceImpl.class);
    private final HttpClient httpClient;
    private final ScheduledExecutorService executorService;
    @Context
    private HttpServletRequest request;

    public NetworkServiceImpl() {
        super(subscriptionDAO);
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
    public Set<String> getTargets(String topic) {
        Subscription example = new Subscription();
        example.setTopic(topic);
        Set<String> targets = new HashSet<>();
        for (Subscription subscription : subscriptionDAO.query(example)) {
            String target = String.format("http://%s:%d/message/?%s=%s", subscription.getIpAddress(), subscription.getPort(), MessageService.SUBSCRIPTION_ID, subscription.getId().toString());
            targets.add(target);
        }
        return targets;
    }

    /**
     * Sends a message for each subscription to the message's topic.
     *
     * @param networkMessage {@link NetworkMessage} message to distribute
     */
    @Override
    public void publish(final NetworkMessage<?> networkMessage) {
        Preconditions.checkArgument(networkMessage.getTopic() != null, "Topic must not be empty");
        Set<String> targets = this.getTargets(networkMessage.getTopic());
        try {
            String body = OBJECT_MAPPER.writeValueAsString(networkMessage);
            final StringEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
            for (final String target : targets) {
                this.executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        HttpPost request = new HttpPost(target);
                        request.setEntity(stringEntity);
                        LOG.debug("Performing a request against: {}", target);
                        try {
                            httpClient.execute(request);
                        } catch (IOException e) {
                            LOG.warn("Erred while sending to a subscribed receiver", e);
                        }
                    }
                });
            }
        } catch (IOException e) {
            LOG.error("Unable to serialize a networkmessage, with id {} and topic {}", networkMessage.getMessageId(), networkMessage.getTopic());
        }
    }

    @Override
    public void extend(UUID subscriptionId) {
        Subscription subscription = this.read(subscriptionId);
        if (subscription != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, DEFAULT_SUBSCRIPTION_EXPIRE_TIME);
            Date date = calendar.getTime();
            subscription.setExpires(date);
            this.save(subscription);
        } else {
            LOG.warn("An attempt was made by a client to extend a subscription with id {}, but no subscription was found for it.", subscriptionId);
        }
    }

    /**
     * Instantiates a subscription, ensures the IP address is not provided by the client.
     * <p/>
     * When the expire date is not provided it will default to local java time + {@link NetworkService#DEFAULT_SUBSCRIPTION_EXPIRE_TIME}.
     *
     * @param instance {@link Subscription} instance whose properties to use for instantiating the entity
     * @return the created entity with its id assigned
     */
    @Override
    public Subscription save(Subscription instance) {
        Preconditions.checkArgument(instance.getIpAddress() == null, "Clients are not permitted to provide the IP-address themselves.");
        Preconditions.checkNotNull(instance.getTopic(), "Clients must provide a topic.");
        Preconditions.checkNotNull(instance.getPort(), "Clients must provide a port.");
        String clientIpAddress = this.request.getRemoteAddr();
        instance.setIpAddress(clientIpAddress);
        if (instance.getExpires() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, DEFAULT_SUBSCRIPTION_EXPIRE_TIME);
            Date date = calendar.getTime();
            instance.setExpires(date);
        }
        return super.save(instance);
    }

}
