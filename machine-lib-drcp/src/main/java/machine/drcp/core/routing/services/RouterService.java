package machine.drcp.core.routing.services;

import machine.drcp.core.api.entities.Message;
import machine.drcp.core.routing.Subscription;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/routing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RouterService {

    public static final int DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS = 5 * 60000;

    @POST
    @Path("/publish")
    public void publish(Message instance);

    /**
     * Extends a subscription duration, by id.
     *
     * @param id id of the subscription to be extended
     */
    @POST
    @Path("/subscription:extend")
    public void extend(@PathParam("id") UUID id);

    /**
     * Instantiates a subscription, ensures the host address is not provided by the client.
     * <p/>
     * When the expire date is not provided it will default to local java time + {@link RouterService#DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS}.
     *
     * @param instance {@link Subscription} instance whose properties to use for instantiating the subscription
     * @return the created subscription with its id assigned
     */
    @POST
    @Path("/subscription")
    public Subscription save(Subscription instance);

    /**
     * Deletes an subscription by id.
     *
     * @param id id of the subscription to be deleted
     */
    @DELETE
    @Path("/subscription/{id}")
    public void delete(@PathParam("id") UUID id);

}
