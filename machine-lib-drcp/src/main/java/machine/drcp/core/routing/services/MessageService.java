package machine.drcp.core.routing.services;

import machine.drcp.core.api.entities.Message;
import machine.drcp.core.routing.exceptions.NotSubscribedException;

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
    public void handle(@QueryParam(SUBSCRIPTION_ID) UUID subscriptionId, Message<?> instance) throws NotSubscribedException;

}
