package machine.management.api.services;

import machine.management.api.entities.Subscriber;
import machine.message.api.entities.NetworkMessage;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/subscribers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SubscriberService extends QueryableModelService<Subscriber> {

    @POST
    @Path("/publish")
    public void publish(NetworkMessage<?> instance);

}
