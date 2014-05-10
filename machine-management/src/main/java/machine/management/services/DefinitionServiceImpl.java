package machine.management.services;

import machine.management.dao.GenericModelDAO;
import machine.management.api.services.DefinitionService;
import machine.management.api.entities.Definition;

public class DefinitionServiceImpl extends AbstractQueryableModelService<Definition> implements DefinitionService {

    private static final GenericModelDAO<Definition> DAO = new GenericModelDAO<>(Definition.class);

    public DefinitionServiceImpl() {
        super(DAO);
    }

}
