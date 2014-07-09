package com.hileco.drcp.http.api;

import com.hileco.drcp.core.api.models.Message;
import com.hileco.drcp.core.api.services.RouterService;
import com.hileco.drcp.http.api.models.HTTPSubscription;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * A {@link com.hileco.drcp.core.api.services.RouterService} with JAX-RS annotations.
 */
@Path("/routing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface JaxrsRouterService extends RouterService<HTTPSubscription> {

    @POST
    @Path("/publish")
    public void publish(Message message);

    @POST
    @Path("/subscription:extend")
    public void extend(@PathParam("id") UUID id);

    @POST
    @Path("/subscription")
    public HTTPSubscription save(HTTPSubscription instance);

    @DELETE
    @Path("/subscription/{id}")
    public void delete(@PathParam("id") UUID id);

}
