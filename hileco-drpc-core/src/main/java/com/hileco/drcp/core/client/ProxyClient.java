package com.hileco.drcp.core.client;

import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hileco.drcp.core.api.Client;
import com.hileco.drcp.core.api.Connector;
import com.hileco.drcp.core.api.exceptions.NotSubscribedException;
import com.hileco.drcp.core.api.models.Message;
import com.hileco.drcp.core.api.models.Subscription;
import com.hileco.drcp.core.api.services.MessageService;
import com.hileco.drcp.core.api.services.RouterService;
import com.hileco.drcp.core.api.util.SilentCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * An implementation of both {@link Client} and {@link MessageService}.
 * <p>
 * This implementation pools subscriptions, and takes care of extending subscriptions in time.
 */
public class ProxyClient implements MessageService, Client {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyClient.class);
    private static final int RESUBSCRIBE_PERIOD_MILLISECONDS = (int) (RouterService.DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS * 0.9);

    private final Supplier<? extends Subscription> subscriptionSupplier;
    private final RouterService routerService;
    private final Map<String, UUID> subscriptionIds;
    private final Multimap<String, Consumer<Message<?>>> topicHandlerIds;
    private final Map<String, Timer> resubscribeTimers;
    private final ObjectConverter objectConverter;

    /**
     * @param subscriptionSupplier a factory for creating subscriptions with optionally implementation specific data
     * @param routerService        networkservice on which to subscribe and publish messages over
     * @param objectConverter      object converter which to use to deserialize messages
     */
    public ProxyClient(Supplier<? extends Subscription> subscriptionSupplier, RouterService routerService, ObjectConverter objectConverter) {
        this.subscriptionSupplier = subscriptionSupplier;
        this.routerService = routerService;
        this.objectConverter = objectConverter;
        this.topicHandlerIds = ArrayListMultimap.create();
        this.subscriptionIds = new HashMap<>();
        this.resubscribeTimers = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SilentCloseable listen(String topic, Consumer<Message<?>> consumer) {
        boolean subscribed = this.topicHandlerIds.get(topic).size() > 0;
        this.topicHandlerIds.put(topic, consumer);
        if (!subscribed) {
            Subscription subscription = subscriptionSupplier.get();
            subscription.setTopic(topic);
            final UUID savedId = this.routerService.save(subscription).getId();
            this.subscriptionIds.put(topic, savedId);
            LOG.debug("Saved subscription {} to topic {}", savedId, topic);
            Timer resubscribeTimer = new Timer(true);
            resubscribeTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    LOG.debug("Extending subscription {} to topic {}", savedId, topic);
                    ProxyClient.this.routerService.extend(savedId);
                }
            }, RESUBSCRIBE_PERIOD_MILLISECONDS, RESUBSCRIBE_PERIOD_MILLISECONDS);
            this.resubscribeTimers.put(topic, resubscribeTimer);
        }
        return () -> {
            boolean change = this.topicHandlerIds.remove(topic, consumer);
            if (change && this.topicHandlerIds.get(topic).isEmpty()) {
                UUID subscriptionId = this.subscriptionIds.remove(topic);
                Timer removed = this.resubscribeTimers.remove(topic);
                removed.cancel();
                LOG.debug("Removing subscription {}", subscriptionId);
                this.routerService.delete(subscriptionId);
            }
        };
    }

    /**
     * Handles a message by delegating it to any registered handlers.
     *
     * @param message message to handle
     * @throws NotSubscribedException when there is no registered callback handler
     */
    @Override
    public void handle(UUID subscriptionId, Message<?> message) throws NotSubscribedException {
        String topic = message.getTopic();
        UUID activeSubscriptionId = subscriptionIds.get(topic);
        if (Objects.equals(activeSubscriptionId, subscriptionId)) {
            LOG.debug("Handling message with topic {}, id {}, subscription {}", message.getTopic(), message.getId(), subscriptionId);
            Lists.newArrayList(this.topicHandlerIds.get(topic)).parallelStream().forEach((consumer) -> {
                try {
                    consumer.accept(message);
                } catch (Exception e) {
                    LOG.error("A handler erred while processing a message", e);
                }
            });
        } else {
            throw new NotSubscribedException(subscriptionId, topic, activeSubscriptionId);
        }
    }

    @Override
    public UUID publish(Message<?> message) {
        if (message.getId() == null) {
            message.setId(UUID.randomUUID());
        }
        LOG.debug("Publishing with topic {}", message.getTopic());
        this.routerService.publish(message);
        return message.getId();
    }

    @Override
    public <T, P> Connector<T, P> connector(Class<T> iface) {
        return new ProxyConnector<>(this, iface, objectConverter);
    }

    @Override
    public <T, P> SilentCloseable listen(Class<T> iface, T implementation, P identifier) {
        String topic = this.topic(iface, identifier);
        ProxyMessageConsumer proxyMessageConsumer = new ProxyMessageConsumer(this, implementation, objectConverter);
        return this.listen(topic, proxyMessageConsumer);
    }

    @Override
    public <T> SilentCloseable listen(Class<T> iface, T implementation) {
        String topic = this.topic(iface);
        ProxyMessageConsumer proxyMessageConsumer = new ProxyMessageConsumer(this, implementation, objectConverter);
        return this.listen(topic, proxyMessageConsumer);
    }

}
