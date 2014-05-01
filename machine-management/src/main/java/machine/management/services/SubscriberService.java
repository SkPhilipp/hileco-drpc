package machine.management.services;

import machine.management.model.Subscriber;
import machine.management.services.generic.ModelService;

import javax.ws.rs.Path;

@Path("/subscribers")
public interface SubscriberService extends ModelService<Subscriber> {

}
