package machine.management.api.services;

import machine.lib.service.services.QueryableModelService;
import machine.management.api.domain.Server;

import javax.ws.rs.Path;

@Path("/servers")
public interface ServerService extends QueryableModelService<Server> {
}
