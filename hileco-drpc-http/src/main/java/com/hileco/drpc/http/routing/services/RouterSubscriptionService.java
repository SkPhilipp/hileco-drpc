package com.hileco.drpc.http.routing.services;

import com.hileco.drpc.http.routing.services.subscriptions.Subscription;
import com.hileco.drpc.http.routing.services.subscriptions.SubscriptionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Philipp Gayret
 */
public class RouterSubscriptionService implements SubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(RouterSubscriptionService.class);

    private final SubscriptionStore subscriptionStore;

    public RouterSubscriptionService(SubscriptionStore subscriptionStore) {
        this.subscriptionStore = subscriptionStore;
    }

    public Subscription save(String topic, String host, Integer port) {
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);
        subscription.setPort(port);
        subscription.setHost(host);
        subscription.setId(UUID.randomUUID());
        return subscriptionStore.save(subscription);
    }

    public void extend(UUID id) {
        Subscription subscription = subscriptionStore.read(id);
        if (subscription != null) {
            this.subscriptionStore.save(subscription);
        } else {
            LOG.warn("An attempt was made by a client to extend a subscription with id {}, but no subscription was found for it.", id);
        }
    }

    public void delete(UUID id) {
        this.subscriptionStore.delete(id);
    }

    @Override
    public Collection<Subscription> withTopic(String topic) {
        return this.subscriptionStore.withTopic(topic);
    }

}
