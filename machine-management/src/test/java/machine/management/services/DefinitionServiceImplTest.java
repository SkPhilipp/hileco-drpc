package machine.management.services;

import machine.management.domain.Definition;
import machine.management.services.helpers.AbstractQueryableModelServiceTester;
import machine.management.services.lib.services.AbstractQueryableModelService;
import org.junit.Assert;

import java.util.UUID;

public class DefinitionServiceImplTest extends AbstractQueryableModelServiceTester<Definition> {

    private DefinitionServiceImpl definitionService = new DefinitionServiceImpl();

    @Override
    public void assertEquals(Definition expected, Definition actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getBottable(), actual.getBottable());
        Assert.assertEquals(expected.getType(), actual.getType());
        Assert.assertEquals(expected.getFormat(), actual.getFormat());
    }

    @Override
    public void randomizeModel(Definition original) {
        original.setBottable(UUID.randomUUID().toString());
        original.setType(UUID.randomUUID().toString());
        original.setFormat(UUID.randomUUID().toString());
    }

    @Override
    public AbstractQueryableModelService<Definition> getQueryableModelServiceImpl() {
        return definitionService;
    }

}
