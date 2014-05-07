package machine.management.api.services;

import machine.management.api.entities.Command;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/remote")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RemoteCommandService {

    /**
     * Returns a machine-backbone property.
     *
     * @param key name of the property
     * @return value of the property
     */
    @GET
    @Path("/property/{key}")
    public String getProperty(@PathParam("key") String key);

    /**
     * Sets a machine-backbone property.
     *
     * @param key name of the property
     * @param value new value of the property
     * @return previous value of the property
     */
    @POST
    @Path("/property/{key}")
    public String setProperty(@PathParam("key") String key, String value);

    /**
     * Executes a machine-backbone command, and returns the result.
     */
    @POST
    @Path("/command")
    public String command(Command command);

}
