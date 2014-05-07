package machine.management.api.services;

import machine.management.api.entities.Definition;
import machine.lib.service.services.QueryableModelService;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/definitions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DefinitionService extends QueryableModelService<Definition> {
}
