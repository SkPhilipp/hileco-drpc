package machine.management.api.services;

import machine.lib.service.services.QueryableModelService;
import machine.management.api.domain.Subscriber;

import javax.ws.rs.Path;

@Path("/subscribers")
public interface SubscriberService extends QueryableModelService<Subscriber> {
}
