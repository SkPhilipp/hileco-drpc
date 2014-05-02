package machine.services;

import machine.management.model.Definition;
import machine.services.lib.services.AbstractQueryableModelService;

import javax.ws.rs.Path;

@Path("/definitions")
public class DefinitionServiceImpl extends AbstractQueryableModelService<Definition> {

}
