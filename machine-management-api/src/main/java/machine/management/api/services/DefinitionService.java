package machine.management.api.services;

import machine.management.api.domain.Definition;
import machine.lib.service.services.QueryableModelService;

import javax.ws.rs.Path;

@Path("/definitions")
public interface DefinitionService extends QueryableModelService<Definition> {
}
