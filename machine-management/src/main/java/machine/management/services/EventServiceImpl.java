package machine.management.services;

import com.google.common.base.Preconditions;
import machine.management.model.Event;
import machine.management.services.lib.services.AbstractQueryableModelService;

import javax.ws.rs.Path;
import java.util.List;
import java.util.UUID;

@Path("/events")
public class EventServiceImpl extends AbstractQueryableModelService<Event> {

    /**
     * Creates an entity, assigns an ID to it.
     *
     * - given event's content must not be empty
     * - given event's topic must not be empty
     * - timestamp will be overridden by default
     *
     * @param instance {@link Event} instance whose properties to use for instantiating the entity
     * @return the {@link java.util.UUID} assigned to the new entity
     */
    @Override
    public UUID create(Event instance){
        Preconditions.checkArgument(instance.getContent() != null, "Content must not be empty");
        Preconditions.checkArgument(instance.getTopic() != null, "Topic must not be empty");
        return super.create(instance);
    }

    /**
     * Queries only when all of the following preconditions are met:
     * - the timestamp is set on the example object
     * - the topic is set on the example
     * - the content is not set on the example object
     *
     * @param example an example instance
     * @return the list of matching events
     */
    @Override
    public List<Event> query(Event example) {
        Preconditions.checkNotNull(example.getTimestamp());
        Preconditions.checkNotNull(example.getTopic());
        Preconditions.checkArgument(example.getContent() == null, "Clients are not permitted to search by content.");
        // TODO: the query should return events _after_ the given timestamp, now it performs matching calls.
        return super.query(example);
    }

}
