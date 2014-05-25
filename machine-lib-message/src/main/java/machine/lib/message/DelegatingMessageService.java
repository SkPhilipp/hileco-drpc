package machine.lib.message;

import com.google.common.collect.Lists;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import machine.message.api.exceptions.NotSubscribedException;
import machine.message.api.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;


/**
 * An implementation of MessageService that delegates to message handlers.
 */
public class DelegatingMessageService extends SubscriptionPoolManager implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingMessageService.class);
    private final NetworkService networkService;

    /**
     * @param localPort      port over which this service is made available
     * @param networkService networkservice on which to subscribe and publish messages over
     */
    public DelegatingMessageService(Integer localPort, NetworkService networkService) {
        super(localPort, networkService);
        this.networkService = networkService;
    }

    /**
     * Handles a message by delegating it to any registered handlers.
     *
     * @param instance message to handle
     * @throws NotSubscribedException when there is no registered callback handler
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handle(UUID subscriptionId, NetworkMessage<?> instance) throws NotSubscribedException {
        String topic = instance.getTopic();
        UUID activeSubscriptionId = this.getSubscriptionId(topic);
        if (Objects.equals(activeSubscriptionId, subscriptionId)) {
            LOG.debug("Handling message with topic {}, id {}, subscription {}", instance.getTopic(), instance.getMessageId(), subscriptionId);
            Collection<MessageHandler<?>> handlers = Lists.newArrayList(this.getHandlers(topic));
            for (MessageHandler<?> handler : handlers) {
                // TODO: handle in threads & catch exceptions
                handler.handle(instance);
            }
        } else {
            throw new NotSubscribedException(subscriptionId, topic, activeSubscriptionId);
        }
    }

    /**
     * @param topic   the message topic
     * @param content the message content
     * @return published message id
     */
    public <T extends Serializable> UUID publish(String topic, T content) {
        NetworkMessage<?> networkMessage = new NetworkMessage<>(topic, content);
        LOG.debug("Publishing with topic {}", topic);
        this.networkService.publish(networkMessage);
        return networkMessage.getMessageId();
    }

    public <T extends Serializable> void publish(NetworkMessage<T> message) {
        this.networkService.publish(message);
    }

}
