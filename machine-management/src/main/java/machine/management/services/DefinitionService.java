package machine.management.services;

import machine.management.model.Definition;
import machine.management.services.generic.ModelService;

import javax.ws.rs.Path;

@Path("/definitions")
public interface DefinitionService extends ModelService<Definition> {

}
