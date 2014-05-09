package machine.management.services;

import com.google.common.base.Preconditions;
import machine.lib.service.dao.GenericModelDAO;
import machine.management.api.entities.Subscriber;
import machine.management.api.receiver.entities.NetworkMessage;
import machine.management.api.receiver.services.MessageService;
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Implementation of {@link MessageService}, which broadcasts received messages to all subscribers to the message's topic.
 */
public class MessageServiceImpl implements MessageService {

    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    public static final int DEFAULT_SENDER_POOL_SIZE = 100;
    private static final GenericModelDAO<Subscriber> subscriberDAO = new GenericModelDAO<>(Subscriber.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final HttpClient httpClient;
    private final ScheduledExecutorService executorService;

    public MessageServiceImpl() {
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

    /**
     * Finds all targets subcribed to the given topic.
     *
     * @param topic an event topic
     * @return all targets subcribed to the given topic
     */
    public Set<String> getTargets(String topic) {
        Subscriber example = new Subscriber();
        example.setTopic(topic);
        Set<String> targets = new HashSet<>();
        for (Subscriber subscriber : subscriberDAO.query(example)) {
            String target = subscriber.getTarget();
            targets.add(target);
        }
        return targets;
    }

    /**
     * Creates a new message, and events for each subscriber to the message's topic, then notifies the event processor.
     *
     * @param networkMessage {@link NetworkMessage} message to distribute
     */
    @Override
    public void handle(final NetworkMessage<?> networkMessage) {
        Preconditions.checkArgument(networkMessage.getContent() != null, "Content must not be empty");
        Preconditions.checkArgument(networkMessage.getTopic() != null, "Topic must not be empty");
        Set<String> targets = this.getTargets(networkMessage.getTopic());
        for (final String target : targets) {
            this.executorService.submit(new Callable<HttpResponse>() {
                @Override
                public HttpResponse call() throws Exception {
                    String body = OBJECT_MAPPER.writeValueAsString(networkMessage);
                    StringEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
                    HttpPost request = new HttpPost(target);
                    request.setEntity(stringEntity);
                    LOG.debug("Performing a request against: {}", target);
                    return httpClient.execute(request);
                }
            });
        }
    }

}
