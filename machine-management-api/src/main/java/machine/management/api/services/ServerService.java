package machine.management.api.services;

import machine.management.api.entities.Server;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/servers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ServerService extends QueryableModelService<Server> {

    @POST
    @Path("/heartbeat")
    public void heartbeat(UUID serverId);

}
