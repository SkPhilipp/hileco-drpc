package machine.router.api.services;

import machine.router.api.entities.NetworkMessage;
import machine.router.api.exceptions.NotSubscribedException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MessageService {

    String SUBSCRIPTION_ID = "subscriptionId";

    @POST
    @Path("/")
    public void handle(@QueryParam(SUBSCRIPTION_ID) UUID subscriptionId, NetworkMessage instance) throws NotSubscribedException;

}
