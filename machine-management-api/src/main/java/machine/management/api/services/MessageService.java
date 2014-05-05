package machine.management.api.services;

import machine.management.api.domain.NetworkMessage;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MessageService {

    @POST
    @Path("/publish")
    public void publish(NetworkMessage<?> instance);

}
