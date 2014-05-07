package machine.management.api.services;

import machine.lib.service.api.services.QueryableModelService;
import machine.management.api.entities.Subscriber;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/subscribers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SubscriberService extends QueryableModelService<Subscriber> {
}
