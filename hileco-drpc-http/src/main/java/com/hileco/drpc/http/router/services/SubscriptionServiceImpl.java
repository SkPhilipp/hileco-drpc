package com.hileco.drpc.http.router.services;

import com.hileco.drpc.http.subscription.Subscription;
import com.hileco.drpc.http.subscription.SubscriptionStore;

import java.util.UUID;

/**
 * @author Philipp Gayret
 */
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionStore subscriptionStore;

    public SubscriptionServiceImpl(SubscriptionStore subscriptionStore) {
        this.subscriptionStore = subscriptionStore;
    }

    @Override
    public Subscription save(String topic, String address) {
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);
        subscription.setAddress(address);
        subscription.setId(UUID.randomUUID());
        return subscriptionStore.save(subscription);
    }

    @Override
    public boolean keepAlive(UUID id) {
        return this.subscriptionStore.keepAlive(id);
    }

    @Override
    public boolean delete(UUID id) {
        return this.subscriptionStore.delete(id);
    }

}
