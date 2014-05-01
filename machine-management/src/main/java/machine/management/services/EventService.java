package machine.management.services;

import machine.management.model.Event;
import machine.management.services.generic.QueryableModelService;

import javax.ws.rs.Path;

@Path("/events")
public interface EventService extends QueryableModelService<Event> {

}
