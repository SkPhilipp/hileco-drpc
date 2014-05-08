package machine.management.api.services;

import machine.management.api.entities.Command;
import machine.management.api.exceptions.CommandExecutionException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/remote")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RemoteCommandService {

    /**
     * Executes a machine-backbone command, and returns the result.
     */
    @POST
    @Path("/command")
    public String command(Command command) throws CommandExecutionException;

}
