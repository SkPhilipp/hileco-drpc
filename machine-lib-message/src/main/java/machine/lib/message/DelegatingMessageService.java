package machine.lib.message;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import machine.lib.message.api.*;
import machine.lib.message.api.util.ObjectConverter;
import machine.lib.message.proxy.ProxyNetworked;
import machine.lib.message.proxy.ProxyNetworkListener;
import machine.lib.message.proxy.ProxyNetworkTopics;
import machine.lib.message.proxy.util.JacksonObjectConverter;
import machine.router.api.entities.NetworkMessage;
import machine.router.api.entities.Subscription;
import machine.router.api.exceptions.NotSubscribedException;
import machine.router.api.services.MessageService;
import machine.router.api.services.RouterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * An implementation of {@link machine.lib.message.api.Network} and {@link MessageService}.
 * <p/>
 * This implementation pools handlers' subscriptions, and keeps track of subscription ids.
 */
public class DelegatingMessageService implements MessageService, Network, NetworkConnector {

    private static final ObjectConverter OBJECT_CONVERTER = new JacksonObjectConverter();
    private static final Logger LOG = LoggerFactory.getLogger(DelegatingMessageService.class);
    private static final int RESUBSCRIBE_PERIOD_MILLISECONDS = (int) (RouterService.DEFAULT_SUBSCRIPTION_EXPIRE_TIME * 0.9 * 60000);
    private final int port;
    private final RouterService routerService;
    private final Map<String, UUID> subscriptionIds;
    private final Multimap<String, Consumer<NetworkMessage>> topicHandlerIds;
    private final Map<String, Timer> resubscribeTimers;

    /**
     * @param localPort     port over which this service is made available
     * @param routerService networkservice on which to subscribe and publish messages over
     */
    public DelegatingMessageService(Integer localPort, RouterService routerService) {
        this.port = localPort;
        this.routerService = routerService;
        this.topicHandlerIds = ArrayListMultimap.create();
        this.subscriptionIds = new HashMap<>();
        this.resubscribeTimers = new HashMap<>();
    }

    public Consumer<NetworkMessage> beginListen(String topic, Consumer<NetworkMessage> networkMessageHandler) {
        boolean subscribed = this.topicHandlerIds.get(topic).size() > 0;
        this.topicHandlerIds.put(topic, networkMessageHandler);
        if (!subscribed) {
            Subscription subscription = new Subscription();
            subscription.setPort(this.port);
            subscription.setTopic(topic);
            Subscription saved = this.routerService.save(subscription);
            final UUID savedId = saved.getId();
            this.subscriptionIds.put(topic, savedId);
            LOG.debug("Saved subscription {} to topic {}", savedId, topic);
            Timer resubscribeTimer = new Timer(true);
            resubscribeTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    LOG.trace("Extending subscription {} to topic {}", savedId, topic);
                    DelegatingMessageService.this.routerService.extend(savedId);
                }
            }, RESUBSCRIBE_PERIOD_MILLISECONDS, RESUBSCRIBE_PERIOD_MILLISECONDS);
            this.resubscribeTimers.put(topic, resubscribeTimer);
        }
        return networkMessageHandler;
    }

    public void stopListen(String topic, Consumer<NetworkMessage> networkMessageHandler) {
        boolean changed = this.topicHandlerIds.remove(topic, networkMessageHandler);
        if (changed) {
            if (this.topicHandlerIds.get(topic).isEmpty()) {
                UUID subscriptionId = this.subscriptionIds.remove(topic);
                Timer removed = this.resubscribeTimers.remove(topic);
                removed.cancel();
                LOG.debug("Removing subscription {}", subscriptionId);
                this.routerService.delete(subscriptionId);
            }
        }
    }

    @Override
    public void stopListen(String topic) {
        Collection<Consumer<NetworkMessage>> removedItems = this.topicHandlerIds.removeAll(topic);
        if (!removedItems.isEmpty()) {
            if (this.topicHandlerIds.get(topic).isEmpty()) {
                UUID subscriptionId = this.subscriptionIds.remove(topic);
                Timer removed = this.resubscribeTimers.remove(topic);
                removed.cancel();
                LOG.debug("Removing subscription {}", subscriptionId);
                this.routerService.delete(subscriptionId);
            }
        }
    }

    /**
     * Handles a message by delegating it to any registered handlers.
     *
     * @param networkMessage message to handle
     * @throws NotSubscribedException when there is no registered callback handler
     */
    @Override
    public void handle(UUID subscriptionId, NetworkMessage networkMessage) throws NotSubscribedException {
        String topic = networkMessage.getTopic();
        UUID activeSubscriptionId = subscriptionIds.get(topic);
        if (Objects.equals(activeSubscriptionId, subscriptionId)) {
            LOG.debug("Handling message with topic {}, id {}, subscription {}", networkMessage.getTopic(), networkMessage.getMessageId(), subscriptionId);
            Lists.newArrayList(this.topicHandlerIds.get(topic)).parallelStream().forEach((consumer) -> {
                try {
                    consumer.accept(networkMessage);
                } catch (Exception e) {
                    LOG.error("A handler erred while processing a message", e);
                }
            });
        } else {
            throw new NotSubscribedException(subscriptionId, topic, activeSubscriptionId);
        }
    }

    public UUID publishMessage(NetworkMessage networkMessage) {
        if (networkMessage.getMessageId() == null) {
            networkMessage.setMessageId(UUID.randomUUID());
        }
        LOG.debug("Publishing with topic {}", networkMessage.getTopic());
        this.routerService.publish(networkMessage);
        return networkMessage.getMessageId();
    }

    @SuppressWarnings("unchecked")
    public <P, T extends InvokeableObject<P>> Function<P, Networked<T>> remoteObject(Class<T> proxyable) {
        return binding -> new ProxyNetworked<>(this, proxyable, binding, OBJECT_CONVERTER);
    }

    @SuppressWarnings("unchecked")
    public <T extends InvokeableService> Networked<T> remoteService(Class<T> proxyable) {
        return new ProxyNetworked<>(this, proxyable, null, OBJECT_CONVERTER);
    }

    public <T extends InvokeableService> void listen(Class<T> iface, T networkService) {
        this.beginListen(ProxyNetworkTopics.getTopic(iface), new ProxyNetworkListener(this, networkService, OBJECT_CONVERTER));
    }

    public <T extends InvokeableObject<P>, P> void listen(Class<T> iface, T networkObject, P binding) {
        this.beginListen(ProxyNetworkTopics.getTopic(iface, binding), new ProxyNetworkListener(this, networkObject, OBJECT_CONVERTER));
    }

    public <T extends InvokeableService> void stopListen(Class<T> iface, T networkService) {
        // TODO: implement
    }

    public <T extends InvokeableObject<P>, P> void stopListen(Class<T> iface, T networkObject, P binding) {
        // TODO: implement
    }

}
