package com.hileco.drcp.http.api;

import com.hileco.drcp.core.api.models.Message;
import com.hileco.drcp.core.api.services.MessageService;
import com.hileco.drcp.core.api.exceptions.NotSubscribedException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * A {@link com.hileco.drcp.core.api.services.MessageService} with JAX-RS annotations.
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
