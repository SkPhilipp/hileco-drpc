package machine.drcp.http.impl.store;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import machine.drcp.core.api.services.RouterService;
import machine.drcp.http.api.models.HTTPSubscription;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * An in-memory implementation of {@link machine.drcp.http.impl.store.SubscriptionStore}, using Guava's Cache and Multimap to map subscriptions
 * by id and topic, and evict them after {@link machine.drcp.core.api.services.RouterService#DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS}.
 */
public class LocalSubscriptionStore implements SubscriptionStore {

    private final Multimap<String, HTTPSubscription> subscriptionByTopicMultimap;
    private final Cache<UUID, HTTPSubscription> subscriptionByIdCache;

    public LocalSubscriptionStore() {
        this.subscriptionByTopicMultimap = HashMultimap.create();
        this.subscriptionByIdCache = CacheBuilder.newBuilder().expireAfterWrite(RouterService.DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS, TimeUnit.MILLISECONDS).removalListener((RemovalNotification<UUID, HTTPSubscription> notification) -> {
            HTTPSubscription removedSubscription = notification.getValue();
            if (removedSubscription != null) {
                this.subscriptionByTopicMultimap.remove(removedSubscription.getTopic(), removedSubscription);
            }
        }).build();
    }

    @Override
    public HTTPSubscription read(UUID id) {
        return subscriptionByIdCache.getIfPresent(id);
    }

    @Override
    public HTTPSubscription save(HTTPSubscription instance) {
        Preconditions.checkNotNull(instance.getTopic());
        Preconditions.checkNotNull(instance.getId());
        subscriptionByTopicMultimap.put(instance.getTopic(), instance);
        subscriptionByIdCache.put(instance.getId(), instance);
        return instance;
    }

    @Override
    public void delete(UUID id) {
        subscriptionByIdCache.invalidate(id);
    }

    @Override
    public Collection<HTTPSubscription> withTopic(String topic) {
        return this.subscriptionByTopicMultimap.get(topic);
    }

}
