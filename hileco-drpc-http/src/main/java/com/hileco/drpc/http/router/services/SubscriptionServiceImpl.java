package com.hileco.drpc.http.router.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

/**
 * {@link SubscriptionStore} as a {@link com.hileco.drpc.http.router.services.SubscriptionService}.
 *
 * @author Philipp Gayret
 */
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private final SubscriptionStore subscriptionStore;

    public SubscriptionServiceImpl(SubscriptionStore subscriptionStore) {
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
