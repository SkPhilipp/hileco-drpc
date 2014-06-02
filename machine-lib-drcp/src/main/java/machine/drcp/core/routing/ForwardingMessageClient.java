package machine.drcp.core.routing;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import machine.drcp.core.api.MessageClient;
import machine.drcp.core.api.entities.Message;
import machine.drcp.core.routing.exceptions.NotSubscribedException;
import machine.drcp.core.routing.services.MessageService;
import machine.drcp.core.routing.services.RouterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;


/**
 * An implementation of both {@link machine.drcp.core.api.MessageClient} and {@link MessageService}.
 * <p>
 * This implementation pools subscriptions, and takes care of extending subscriptions in time.
 */
public class ForwardingMessageClient implements MessageService, MessageClient {

    private static final Logger LOG = LoggerFactory.getLogger(ForwardingMessageClient.class);
    private static final int RESUBSCRIBE_PERIOD_MILLISECONDS = (int) (RouterService.DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS * 0.9);

    private final int port;
    private final RouterService routerService;
    private final Map<String, UUID> subscriptionIds;
    private final Multimap<String, Consumer<Message<?>>> topicHandlerIds;
    private final Map<String, Timer> resubscribeTimers;

    /**
     * @param localPort     port over which this service to be is made available
     * @param routerService networkservice on which to subscribe and publish messages over
     */
    public ForwardingMessageClient(Integer localPort, RouterService routerService) {
        this.port = localPort;
        this.routerService = routerService;
        this.topicHandlerIds = ArrayListMultimap.create();
        this.subscriptionIds = new HashMap<>();
        this.resubscribeTimers = new HashMap<>();
    }

    @Override
    public AutoCloseable listen(String topic, Consumer<Message<?>> consumer) {
        boolean subscribed = this.topicHandlerIds.get(topic).size() > 0;
        this.topicHandlerIds.put(topic, consumer);
        if (!subscribed) {
            Subscription subscription = new Subscription();
            subscription.setPort(this.port);
            subscription.setTopic(topic);
            Subscription saved = this.routerService.save(subscription);
            final UUID savedId = saved.getId();
            this.subscriptionIds.put(topic, savedId);
            LOG.debug("Saved subscription {} to topic {}", savedId, topic);
            Timer resubscribeTimer = new Timer(true);
            resubscribeTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    LOG.debug("Extending subscription {} to topic {}", savedId, topic);
                    ForwardingMessageClient.this.routerService.extend(savedId);
                }
            }, RESUBSCRIBE_PERIOD_MILLISECONDS, RESUBSCRIBE_PERIOD_MILLISECONDS);
            this.resubscribeTimers.put(topic, resubscribeTimer);
        }
        return () -> {
            boolean change = this.topicHandlerIds.remove(topic, consumer);
            if (change && this.topicHandlerIds.get(topic).isEmpty()) {
                UUID subscriptionId = this.subscriptionIds.remove(topic);
                Timer removed = this.resubscribeTimers.remove(topic);
                removed.cancel();
                LOG.debug("Removing subscription {}", subscriptionId);
                this.routerService.delete(subscriptionId);
            }
        };
    }

    /**
     * Handles a message by delegating it to any registered handlers.
     *
     * @param Message message to handle
     * @throws NotSubscribedException when there is no registered callback handler
     */
    @Override
    public void handle(UUID subscriptionId, Message<?> Message) throws NotSubscribedException {
        String topic = Message.getTopic();
        UUID activeSubscriptionId = subscriptionIds.get(topic);
        if (Objects.equals(activeSubscriptionId, subscriptionId)) {
            LOG.debug("Handling message with topic {}, id {}, subscription {}", Message.getTopic(), Message.getMessageId(), subscriptionId);
            Lists.newArrayList(this.topicHandlerIds.get(topic)).parallelStream().forEach((consumer) -> {
                try {
                    consumer.accept(Message);
                } catch (Exception e) {
                    LOG.error("A handler erred while processing a message", e);
                }
            });
        } else {
            throw new NotSubscribedException(subscriptionId, topic, activeSubscriptionId);
        }
    }

    @Override
    public UUID publish(Message<?> message) {
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID());
        }
        LOG.debug("Publishing with topic {}", message.getTopic());
        this.routerService.publish(message);
        return message.getMessageId();
    }

}
