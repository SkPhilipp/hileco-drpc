package machine.router.api.services;

import machine.router.api.entities.NetworkMessage;
import machine.router.api.entities.Subscription;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Path("/routing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RouterService {

    public static final int DEFAULT_SUBSCRIPTION_EXPIRE_TIME = 5;
    public static final TimeUnit DEFAULT_SUBSCRIPTION_EXPIRE_UNIT = TimeUnit.MINUTES;

    @POST
    @Path("/publish")
    public void publish(NetworkMessage instance);

    /**
     * Extends a subscription duration, by id.
     *
     * @param id id of the subscription to be extended
     */
    @POST
    @Path("/extend")
    public void extend(@PathParam("id") UUID id);

    /**
     * Instantiates a subscription, ensures the host address is not provided by the client.
     * <p/>
     * When the expire date is not provided it will default to local java time + {@link RouterService#DEFAULT_SUBSCRIPTION_EXPIRE_TIME}.
     *
     * @param instance {@link Subscription} instance whose properties to use for instantiating the subscription
     * @return the created subscription with its id assigned
     */
    @POST
    @Path("/subscription")
    public Subscription save(Subscription instance);

    /**
     * Finds an subscription by id.
     *
     * @param id id of the subscription to be found.
     * @return matching subscription or null.
     */
    @GET
    @Path("/subscription/{id}")
    public Subscription read(@PathParam("id") UUID id);

    /**
     * Deletes an subscription by id.
     *
     * @param id id of the subscription to be deleted
     */
    @DELETE
    @Path("/subscription/{id}")
    public void delete(@PathParam("id") UUID id);

}
