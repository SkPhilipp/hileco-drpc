package machine.management.services;

import com.google.common.base.Preconditions;
import machine.lib.client.messaging.NetworkMessage;
import machine.lib.client.messaging.NetworkMessageRouter;
import machine.lib.service.dao.GenericModelDAO;
import machine.management.api.domain.Subscriber;
import machine.management.api.services.MessageService;

import java.util.HashSet;
import java.util.Set;

public class MessageServiceImpl implements MessageService {

    private static final GenericModelDAO<Subscriber> subscriberDAO = new GenericModelDAO<>(Subscriber.class);
    private final NetworkMessageRouter networkMessageRouter;

    public MessageServiceImpl() {
        this.networkMessageRouter = new NetworkMessageRouter();
    }

    /**
     * Finds all targets subcribed to the given topic.
     *
     * @param topic an event topic
     * @return all targets subcribed to the given topic
     */
    public Set<String> getTargets(String topic) {
        Subscriber example = new Subscriber();
        example.setTopic(topic);
        Set<String> targets = new HashSet<>();
        for (Subscriber subscriber : subscriberDAO.query(example)) {
            String target = subscriber.getTarget();
            targets.add(target);
        }
        return targets;
    }

    /**
     * Creates a new message, and events for each subscriber to the message's topic, then notifies the event processor.
     *
     * @param instance {@link NetworkMessage} message to distribute
     */
    public void publish(NetworkMessage<?> instance) {
        Preconditions.checkArgument(instance.getContent() != null, "Content must not be empty");
        Preconditions.checkArgument(instance.getTopic() != null, "Topic must not be empty");
        Set<String> targets = this.getTargets(instance.getTopic());
        for (String target : targets) {
            networkMessageRouter.submit(target, instance);
        }
    }

}
