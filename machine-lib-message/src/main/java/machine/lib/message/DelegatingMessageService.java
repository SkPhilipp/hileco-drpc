package machine.lib.message;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import machine.lib.message.api.Network;
import machine.management.api.entities.Subscription;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import machine.message.api.exceptions.NotSubscribedException;
import machine.message.api.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;


/**
 * An implementation of {@link machine.lib.message.api.Network} and {@link MessageService}.
 *
 * This implementation pools handlers' subscriptions, and keeps track of subscription ids.
 */
public class DelegatingMessageService implements MessageService, Network {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingMessageService.class);
    private static final int RESUBSCRIBE_PERIOD_MILLISECONDS = (int) (NetworkService.DEFAULT_SUBSCRIPTION_EXPIRE_TIME * 0.9 * 60000);
    private final int port;
    private final NetworkService networkService;
    private final Map<String, UUID> subscriptionIds;
    private final Multimap<String, Consumer<NetworkMessage>> topicHandlerIds;
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

    public Consumer<NetworkMessage> beginListen(String topic, Consumer<NetworkMessage> networkMessageHandler) {
        boolean subscribed = this.topicHandlerIds.get(topic).size() > 0;
        this.topicHandlerIds.put(topic, networkMessageHandler);
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
        return networkMessageHandler;
    }

    public void stopListen(String topic, Consumer<NetworkMessage> networkMessageHandler) {
        boolean changed = this.topicHandlerIds.remove(topic, networkMessageHandler);
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

    @Override
    public void stopListen(String topic) {
        Collection<Consumer<NetworkMessage>> removedItems = this.topicHandlerIds.removeAll(topic);
        if (!removedItems.isEmpty()) {
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
     * @param networkMessage message to handle
     * @throws NotSubscribedException when there is no registered callback handler
     */
    @Override
    public void handle(UUID subscriptionId, NetworkMessage<?> networkMessage) throws NotSubscribedException {
        String topic = networkMessage.getTopic();
        UUID activeSubscriptionId = subscriptionIds.get(topic);
        if (Objects.equals(activeSubscriptionId, subscriptionId)) {
            LOG.debug("Handling message with topic {}, id {}, subscription {}", networkMessage.getTopic(), networkMessage.getMessageId(), subscriptionId);
            Lists.newArrayList(this.topicHandlerIds.get(topic)).parallelStream().forEach((consumer) -> {
                try {
                    consumer.accept(networkMessage);
                } catch (Exception e) {
                    LOG.error("A handler erred while processing a message", e);
                }
            });
        } else {
            throw new NotSubscribedException(subscriptionId, topic, activeSubscriptionId);
        }
    }

    public <T extends Serializable> UUID publishMessage(String topic, T content) {
        NetworkMessage<T> networkMessage = new NetworkMessage<>(topic, content);
        LOG.debug("Publishing with topic {}", topic);
        this.networkService.publish(networkMessage);
        return networkMessage.getMessageId();
    }

    public <T extends Serializable> void publishCustom(NetworkMessage<T> message) {
        this.networkService.publish(message);
    }

}
