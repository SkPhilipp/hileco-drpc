package com.hileco.drpc.http.router.services;

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
 * by id and topic, and evict them after {@link #DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS}.
 *
 * @author Philipp Gayret
 */
public class CacheSubscriptionStore implements SubscriptionStore {

    private static final Long DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS = 60000l;

    private final Multimap<String, Subscription> subscriptionByTopicMultimap;
    private final Cache<UUID, Subscription> subscriptionByIdCache;

    public CacheSubscriptionStore() {
        this.subscriptionByTopicMultimap = HashMultimap.create();
        this.subscriptionByIdCache = CacheBuilder.newBuilder()
                .expireAfterWrite(DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS, TimeUnit.MILLISECONDS)
                .removalListener((RemovalNotification<UUID, Subscription> notification) -> {
                    Subscription removedSubscription = notification.getValue();
                    if (removedSubscription != null) {
                        this.subscriptionByTopicMultimap.remove(removedSubscription.getTopic(), removedSubscription);
                    }
                })
                .build();
    }

    @Override
    public synchronized Subscription read(UUID id) {
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
    public synchronized void delete(UUID id) {
        subscriptionByIdCache.invalidate(id);
    }

    @Override
    public synchronized Collection<Subscription> withTopic(String topic) {
        return Lists.newArrayList(this.subscriptionByTopicMultimap.get(topic));
    }

}
