package machine.management.services;

import com.google.common.base.Preconditions;
import machine.management.domain.Event;
import machine.management.domain.Message;
import machine.management.domain.Subscriber;
import machine.management.processes.EventProcessor;
import machine.management.services.lib.dao.GenericModelDAO;
import machine.management.services.lib.services.AbstractQueryableModelService;

import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Path("/messages")
public class MessageServiceImpl extends AbstractQueryableModelService<Message> {

    private static final GenericModelDAO<Message> messageDAO = new GenericModelDAO<>(Message.class);
    private static final GenericModelDAO<Subscriber> subscriberDAO = new GenericModelDAO<>(Subscriber.class);
    private static final GenericModelDAO<Event> eventDAO = new GenericModelDAO<>(Event.class);
    private final EventProcessor eventProcessor = EventProcessor.getInstance();

    public MessageServiceImpl() {
        super(messageDAO);
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
     * Creates an entity, assigns an ID to it.
     * <p/>
     * - given event's content must not be empty
     * - given event's topic must not be empty
     * - timestamp will be overridden by default
     *
     * @param instance {@link machine.management.domain.Message} instance whose properties to use for instantiating the entity
     * @return the {@link java.util.UUID} assigned to the new entity
     */
    @Override
    public UUID create(Message instance) {
        Preconditions.checkArgument(instance.getContent() != null, "Content must not be empty");
        Preconditions.checkArgument(instance.getTopic() != null, "Topic must not be empty");
        Preconditions.checkArgument(instance.getTimestamp() == null, "Timestamp may not be provided by clients");
        instance.setTimestamp(System.currentTimeMillis());
        UUID instanceId = super.create(instance);
        Set<String> targets = this.getTargets(instance.getTopic());
        if (!targets.isEmpty()) {
            messageDAO.create(instance);
            for (String target : targets) {
                Event event = new Event();
                event.setMessage(instanceId);
                event.setTimestamp(instance.getTimestamp());
                event.setTarget(target);
                eventDAO.create(event);
            }
            eventProcessor.alert();
        }
        return instanceId;
    }

    @Override
    public void update(Message instance) {
        throw new UnsupportedOperationException("Messages may not be updated.");
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException("Messages may not be deleted.");
    }

}
