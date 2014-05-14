package machine.lib.message;

import machine.management.api.entities.Subscription;
import machine.management.api.services.NetworkService;

import java.util.*;

/**
 * Handles re-subscribing and unsubscribing.
 *
 * ( As all subscriptions are automatically deleted after {@link NetworkService#DEFAULT_SUBSCRIPTION_EXPIRE_TIME}. )
 */
public class SubscriptionPool {

    private final NetworkService networkService;
    private int port;
    private final Map<String, UUID> subscriptionIds;
    private final Map<String, Timer> resubscribeTimers;
    private final Map<String, Timer> unsubscribeTimers;

    public SubscriptionPool(NetworkService networkService, int port){
        this.networkService = networkService;
        this.port = port;
        this.subscriptionIds = new HashMap<>();
        this.resubscribeTimers = new HashMap<>();
        this.unsubscribeTimers = new HashMap<>();
    }

    /**
     * @return the time in milliseconds after which this implementation resubscribes
     */
    public long getResubscribeMillis(){
        return (long) (NetworkService.DEFAULT_SUBSCRIPTION_EXPIRE_TIME * 0.9 * 60000);
    }

    /**
     * Creates an indefinite subscription that will be refreshed until {@link #unsubscribe(String)}'d.
     *
     * @param topic message topic
     * @return {@link NetworkService}-created subscription
     */
    public Subscription subscribe(String topic) {
        // create a subscription at the networkservice
        Subscription subscription = new Subscription();
        subscription.setPort(this.port);
        subscription.setTopic(topic);
        Subscription saved = networkService.save(subscription);
        final UUID savedId = saved.getId();
        // schedule the refreshing timer
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                SubscriptionPool.this.networkService.extend(savedId);
            }
        }, 0, getResubscribeMillis());
        // store them both
        this.subscriptionIds.put(topic, savedId);
        this.resubscribeTimers.put(topic, timer);
        return saved;
    }

    /**
     * Creates a subscription that will be refreshed until {@link #unsubscribe(String)}'d or timed out.
     *
     * @param topic message topic
     * @param timeout timeout time in milliseconds
     * @return {@link NetworkService}-created subscription
     */
    public Subscription subscribeFor(final String topic, int timeout) {
        Subscription subscription = this.subscribe(topic);
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                SubscriptionPool.this.unsubscribe(topic);
            }
        }, 0, getResubscribeMillis());
        this.unsubscribeTimers.put(topic, timer);
        return subscription;
    }

    /**
     * Unsubscribes if subscribed to a topic.
     *
     * @param topic message topic
     */
    public void unsubscribe(String topic) {
        if(this.isSubscribed(topic)){
            UUID id = this.subscriptionIds.remove(topic);
            Timer resubscribeTimer = this.resubscribeTimers.get(topic);
            if(resubscribeTimer != null){
                resubscribeTimer.cancel();
            }
            Timer unsubscribeTimer = this.unsubscribeTimers.get(topic);
            if(unsubscribeTimer != null){
                unsubscribeTimer.cancel();
            }
            this.networkService.delete(id);
        }
    }

    /**
     *
     * @param topic message topic
     * @return true when a subscription exists
     */
    public boolean isSubscribed(String topic) {
        return this.subscriptionIds.containsKey(topic);
    }

}
