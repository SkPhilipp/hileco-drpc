package machine.management.services;

import machine.management.domain.Definition;
import machine.management.services.lib.dao.GenericModelDAO;
import machine.management.services.lib.services.AbstractQueryableModelService;

import javax.ws.rs.Path;

@Path("/definitions")
public class DefinitionServiceImpl extends AbstractQueryableModelService<Definition> {

    private static final GenericModelDAO<Definition> DAO = new GenericModelDAO<Definition>(Definition.class);

    public DefinitionServiceImpl() {
        super(DAO);
    }

}
