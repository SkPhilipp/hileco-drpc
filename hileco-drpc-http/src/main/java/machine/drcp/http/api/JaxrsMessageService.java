package machine.drcp.http.api;

import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.services.MessageService;
import machine.drcp.core.api.exceptions.NotSubscribedException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * A {@link machine.drcp.core.api.services.MessageService} with JAX-RS annotations.
 */
@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface JaxrsMessageService extends MessageService {

    String SUBSCRIPTION_ID = "subscriptionId";

    @POST
    @Path("/")
    public void handle(@QueryParam(SUBSCRIPTION_ID) UUID subscriptionId, Message<?> instance) throws NotSubscribedException;

}
