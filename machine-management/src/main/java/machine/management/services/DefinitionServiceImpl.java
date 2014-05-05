package machine.management.services;

import machine.management.domain.Definition;
import machine.lib.service.dao.GenericModelDAO;
import machine.lib.service.services.AbstractQueryableModelService;

import javax.ws.rs.Path;

@Path("/definitions")
public class DefinitionServiceImpl extends AbstractQueryableModelService<Definition> {

    private static final GenericModelDAO<Definition> DAO = new GenericModelDAO<>(Definition.class);

    public DefinitionServiceImpl() {
        super(DAO);
    }

}
