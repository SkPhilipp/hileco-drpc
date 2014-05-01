package machine.management.services;

import machine.management.model.Definition;
import machine.management.services.generic.QueryableModelService;

import javax.ws.rs.Path;

@Path("/definitions")
public interface DefinitionService extends QueryableModelService<Definition> {

}
