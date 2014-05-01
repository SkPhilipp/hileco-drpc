package machine.management.services;

import com.google.common.base.Preconditions;
import machine.management.model.Event;
import machine.management.services.generic.AbstractQueryableModelService;

import java.util.List;

public class EventServiceImpl extends AbstractQueryableModelService<Event> implements EventService {

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
        Preconditions.checkArgument(example.getContent()== null, "Clients are not permitted to search by content.");
        // TODO: the query should return events _after_ the given timestamp, now it performs matching calls.
        return super.query(example);
    }

}
