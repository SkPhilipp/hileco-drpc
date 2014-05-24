package machine.lib.message;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import machine.management.api.entities.Subscription;
import machine.management.api.services.NetworkService;

import java.util.*;

public class SubscriptionPoolManager {

    public static final int RESUBSCRIBE_PERIOD_MILLISECONDS = (int) (NetworkService.DEFAULT_SUBSCRIPTION_EXPIRE_TIME * 0.9 * 60000);
    private final int port;
    private final NetworkService networkService;
    private final Map<String, UUID> subscriptionIds;
    private final Multimap<String, MessageHandler<?>> topicHandlerIds;
    private Map<String, Timer> resubscribeTimers;

    public SubscriptionPoolManager(int port, NetworkService networkService) {
        this.port = port;
        this.networkService = networkService;
        this.topicHandlerIds = ArrayListMultimap.create();
        this.subscriptionIds = new HashMap<>();
        this.resubscribeTimers = new HashMap<>();
    }

    /**
     * Registers a handler for messages of a topic and ensures subscription to the topic until removed.
     *
     * @param topic                 the message topic
     * @param messageHandler the message handler
     */
    public void registerHandler(String topic, MessageHandler<?> messageHandler) {
        boolean subscribed = this.topicHandlerIds.get(topic).size() > 0;
        this.topicHandlerIds.put(topic, messageHandler);
        if (!subscribed) {
            Subscription subscription = new Subscription();
            subscription.setPort(this.port);
            subscription.setTopic(topic);
            Subscription saved = networkService.save(subscription);
            final UUID savedId = this.subscriptionIds.put(topic, saved.getId());
            Timer resubscribeTimer = new Timer(true);
            resubscribeTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    SubscriptionPoolManager.this.networkService.extend(savedId);
                }
            }, RESUBSCRIBE_PERIOD_MILLISECONDS, RESUBSCRIBE_PERIOD_MILLISECONDS);
            this.resubscribeTimers.put(topic, resubscribeTimer);
        }
    }

    /**
     * Removes a handler of messages, and if this is the last handler for that topic it will unsubscribe and cancel
     * the internal resubscribe timer.
     *
     * @param topic                 the topic of the registered id
     * @param messageHandler the message handler to remove
     */
    public void removeHandler(String topic, MessageHandler<?> messageHandler) {
        boolean changed = this.topicHandlerIds.remove(topic, messageHandler);
        if (changed) {
            if (this.topicHandlerIds.get(topic).isEmpty()) {
                UUID subscriptionId = this.subscriptionIds.remove(topic);
                Timer removed = this.resubscribeTimers.remove(topic);
                removed.cancel();
                this.networkService.delete(subscriptionId);
            }
        }
    }

    /**
     * @param topic a message topic
     * @return the reference of an active subscription
     */
    public UUID getSubscriptionId(String topic) {
        return subscriptionIds.get(topic);
    }

    /**
     * @param topic a message topic
     * @return all handlers for a given topic
     */
    public Collection<MessageHandler<?>> getHandlers(String topic) {
        return this.topicHandlerIds.get(topic);
    }

}
