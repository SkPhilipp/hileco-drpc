package com.hileco.drpc.http.router.services;

import com.hileco.drpc.http.router.subscription.Subscription;
import com.hileco.drpc.http.router.subscription.SubscriptionStore;

import java.util.Collection;
import java.util.UUID;

/**
 * {@link com.hileco.drpc.http.router.subscription.SubscriptionStore} as a {@link com.hileco.drpc.http.router.services.SubscriptionService}.
 *
 * @author Philipp Gayret
 */
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionStore subscriptionStore;

    public SubscriptionServiceImpl(SubscriptionStore subscriptionStore) {
        this.subscriptionStore = subscriptionStore;
    }

    public Subscription save(String topic, String address) {
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);
        subscription.setAddress(address);
        subscription.setId(UUID.randomUUID());
        return subscriptionStore.save(subscription);
    }

    public boolean extend(UUID id) {
        return this.subscriptionStore.extend(id);
    }

    public boolean delete(UUID id) {
        return this.subscriptionStore.delete(id);
    }

    @Override
    public Collection<Subscription> withTopic(String topic) {
        return this.subscriptionStore.withTopic(topic);
    }

}
