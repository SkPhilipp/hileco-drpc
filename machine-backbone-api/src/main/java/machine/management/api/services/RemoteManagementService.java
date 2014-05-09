package machine.management.api.services;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;


@Path("/configuration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RemoteManagementService {

    /**
     * @return the local machine's registered server id.
     */
    @GET
    @Path("/server-id")
    public UUID getServerId();

    /**
     * @return the current URL to the management api.
     */
    @GET
    @Path("/management-url")
    public String getManagementURL();

    /**
     * Configures the current management URL.
     *
     * @param managementURL new management URL value
     */
    @POST
    @Path("/management-url")
    public void setManagementURL(String managementURL);

}
