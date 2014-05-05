package machine.management.services;

import com.google.common.base.Preconditions;
import machine.lib.client.messaging.NetworkMessage;
import machine.lib.client.messaging.NetworkMessageRouter;
import machine.lib.client.messaging.NetworkMessageSerializer;
import machine.lib.service.dao.GenericModelDAO;
import machine.management.domain.Event;
import machine.management.domain.Message;
import machine.management.domain.Subscriber;
import machine.management.processes.EventProcessor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageServiceImpl {

    private static final GenericModelDAO<Message> messageDAO = new GenericModelDAO<>(Message.class);
    private static final GenericModelDAO<Subscriber> subscriberDAO = new GenericModelDAO<>(Subscriber.class);
    private static final GenericModelDAO<Event> eventDAO = new GenericModelDAO<>(Event.class);

    private final EventProcessor eventProcessor = EventProcessor.getInstance();
    private final NetworkMessageSerializer networkMessageSerializer = NetworkMessageRouter.DEFAULT_SERIALIZER;

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
     * @return the {@link java.util.UUID} assigned to the stored message
     */
    @POST
    @Path("/publish")
    public UUID publish(NetworkMessage<?> instance) {
        Preconditions.checkArgument(instance.getContent() != null, "Content must not be empty");
        Preconditions.checkArgument(instance.getTopic() != null, "Topic must not be empty");
        Message message = new Message();
        message.setTimestamp(System.currentTimeMillis());
        try {
            message.setContent(networkMessageSerializer.serializeToBytes(instance));
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to serialize the incoming network message", e);
        }
        messageDAO.create(message);
        Set<String> targets = this.getTargets(instance.getTopic());
        if (!targets.isEmpty()) {
            for (String target : targets) {
                Event event = new Event();
                event.setMessage(message.getId());
                event.setTimestamp(message.getTimestamp());
                event.setTarget(target);
                eventDAO.create(event);
            }
            eventProcessor.alert();
        }
        return message.getId();
    }

}
