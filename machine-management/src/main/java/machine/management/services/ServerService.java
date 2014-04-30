package machine.management.services;

import machine.management.model.Server;
import machine.management.services.generic.ModelService;

import javax.ws.rs.Path;

@Path("/servers")
public interface ServerService extends ModelService<Server> {

}
