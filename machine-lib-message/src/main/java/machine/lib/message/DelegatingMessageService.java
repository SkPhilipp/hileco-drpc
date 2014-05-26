package machine.lib.message;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import machine.management.api.entities.Subscription;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import machine.message.api.exceptions.NotSubscribedException;
import machine.message.api.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;


/**
 * An implementation of MessageService that delegates to message handlers.
 */
public class DelegatingMessageService implements MessageService, Network {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingMessageService.class);
    private static final int RESUBSCRIBE_PERIOD_MILLISECONDS = (int) (NetworkService.DEFAULT_SUBSCRIPTION_EXPIRE_TIME * 0.9 * 60000);
    private final int port;
    private final NetworkService networkService;
    private final Map<String, UUID> subscriptionIds;
    private final Multimap<String, MessageHandler<?>> topicHandlerIds;
    private final Map<String, Timer> resubscribeTimers;

    /**
     * @param localPort      port over which this service is made available
     * @param networkService networkservice on which to subscribe and publish messages over
     */
    public DelegatingMessageService(Integer localPort, NetworkService networkService) {
        this.port = localPort;
        this.networkService = networkService;
        this.topicHandlerIds = ArrayListMultimap.create();
        this.subscriptionIds = new HashMap<>();
        this.resubscribeTimers = new HashMap<>();
    }

    /**
     * Registers a handler for messages of a topic and ensures subscription to the topic until removed.
     *
     * @param topic          the message topic
     * @param messageHandler the message handler
     */
    public void beginListen(String topic, MessageHandler<?> messageHandler) {
        boolean subscribed = this.topicHandlerIds.get(topic).size() > 0;
        this.topicHandlerIds.put(topic, messageHandler);
        if (!subscribed) {
            Subscription subscription = new Subscription();
            subscription.setPort(this.port);
            subscription.setTopic(topic);
            Subscription saved = this.networkService.save(subscription);
            final UUID savedId = saved.getId();
            this.subscriptionIds.put(topic, savedId);
            LOG.debug("Saved subscription {} to topic {}", savedId, topic);
            Timer resubscribeTimer = new Timer(true);
            resubscribeTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    LOG.trace("Extending subscription {} to topic {}", savedId, topic);
                    DelegatingMessageService.this.networkService.extend(savedId);
                }
            }, RESUBSCRIBE_PERIOD_MILLISECONDS, RESUBSCRIBE_PERIOD_MILLISECONDS);
            this.resubscribeTimers.put(topic, resubscribeTimer);
        }
    }

    /**
     * Removes a handler of messages, and if this is the last handler for that topic it will unsubscribe and cancel
     * the internal resubscribe timer.
     *
     * @param topic          the topic of the registered id
     * @param messageHandler the message handler to remove
     */
    public void stopListen(String topic, MessageHandler<?> messageHandler) {
        boolean changed = this.topicHandlerIds.remove(topic, messageHandler);
        if (changed) {
            if (this.topicHandlerIds.get(topic).isEmpty()) {
                UUID subscriptionId = this.subscriptionIds.remove(topic);
                Timer removed = this.resubscribeTimers.remove(topic);
                removed.cancel();
                LOG.debug("Removing subscription {}", subscriptionId);
                this.networkService.delete(subscriptionId);
            }
        }
    }

    /**
     * Handles a message by delegating it to any registered handlers.
     *
     * @param instance message to handle
     * @throws NotSubscribedException when there is no registered callback handler
     */
    @Override
    public void handle(UUID subscriptionId, NetworkMessage<?> instance) throws NotSubscribedException {
        String topic = instance.getTopic();
        UUID activeSubscriptionId = subscriptionIds.get(topic);
        if (Objects.equals(activeSubscriptionId, subscriptionId)) {
            LOG.debug("Handling message with topic {}, id {}, subscription {}", instance.getTopic(), instance.getMessageId(), subscriptionId);
            Collection<MessageHandler<?>> handlers = Lists.newArrayList(this.topicHandlerIds.get(topic));
            for (MessageHandler<?> handler : handlers) {
                // TODO: handle in threads & catch exceptions
                handler.handle(instance);
            }
        } else {
            throw new NotSubscribedException(subscriptionId, topic, activeSubscriptionId);
        }
    }

    /**
     * @param topic   the message topic
     * @param content the message content
     * @return published message id
     */
    public <T extends Serializable> UUID publishMessage(String topic, T content) {
        NetworkMessage<?> networkMessage = new NetworkMessage<>(topic, content);
        LOG.debug("Publishing with topic {}", topic);
        this.networkService.publish(networkMessage);
        return networkMessage.getMessageId();
    }

    public <T extends Serializable> void publishCustom(NetworkMessage<T> message) {
        this.networkService.publish(message);
    }

}
