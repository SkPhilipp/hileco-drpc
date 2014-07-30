package com.hileco.drpc.http.subscription;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * An in-memory implementation of {@link SubscriptionStore}, using Guava's Cache and Multimap to map subscriptions
 * by id and topic, and evict them after {@link #DEFAULT_TIMEOUT_MS}.
 *
 * CacheSubscriptionStore is threadsafe.
 *
 * @author Philipp Gayret
 */
public class CacheSubscriptionStore implements SubscriptionStore {

    private final Multimap<String, Subscription> subscriptionByTopicMultimap;
    private final Cache<UUID, Subscription> subscriptionByIdCache;

    public CacheSubscriptionStore() {
        this.subscriptionByTopicMultimap = HashMultimap.create();
        this.subscriptionByIdCache = CacheBuilder.newBuilder()
                .expireAfterAccess(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .removalListener((RemovalNotification<UUID, Subscription> notification) -> {
                    Subscription removedSubscription = notification.getValue();
                    if (removedSubscription != null) {
                        this.subscriptionByTopicMultimap.remove(removedSubscription.getTopic(), removedSubscription);
                    }
                })
                .build();
    }

    private synchronized Subscription read(UUID id) {
        return subscriptionByIdCache.getIfPresent(id);
    }

    @Override
    public synchronized Subscription save(Subscription instance) {
        Preconditions.checkNotNull(instance.getTopic());
        Preconditions.checkNotNull(instance.getId());
        subscriptionByTopicMultimap.put(instance.getTopic(), instance);
        subscriptionByIdCache.put(instance.getId(), instance);
        return instance;
    }

    @Override
    public synchronized boolean keepAlive(UUID id) {
        Subscription subscription = this.read(id);
        boolean exists = subscription != null;
        if (exists) {
            this.save(subscription);
        }
        return exists;
    }

    @Override
    public synchronized boolean delete(UUID id) {
        boolean exists = this.read(id) != null;
        if (exists) {
            subscriptionByIdCache.invalidate(id);
        }
        return exists;
    }

    @Override
    public synchronized Collection<Subscription> withTopic(String topic) {
        return Lists.newArrayList(this.subscriptionByTopicMultimap.get(topic));
    }

}
