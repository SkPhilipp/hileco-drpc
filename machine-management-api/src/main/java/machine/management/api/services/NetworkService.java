package machine.management.api.services;

import machine.management.api.entities.Subscription;
import machine.message.api.entities.NetworkMessage;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/network")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface NetworkService extends QueryableModelService<Subscription> {

    public static final int DEFAULT_SUBSCRIPTION_EXPIRE_TIME = 5;

    @POST
    @Path("/publish")
    public void publish(NetworkMessage<?> instance);

    @POST
    @Path("/extend")
    public void extend(UUID subscriptionId);

}
