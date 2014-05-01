package machine.management.services;

import machine.management.model.Definition;
import machine.management.services.generic.AbstractModelService;
import org.junit.Assert;

import java.util.UUID;

public class DefinitionServiceImplTest extends AbstractModelServiceTester<Definition> {

    private DefinitionServiceImpl definitionService = new DefinitionServiceImpl();

    @Override
    public void assertEquals(Definition expected, Definition actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getBottable(), actual.getBottable());
        Assert.assertEquals(expected.getType(), actual.getType());
        Assert.assertEquals(expected.getFormat(), actual.getFormat());
    }

    @Override
    public void randomize(Definition original) {
        original.setBottable(UUID.randomUUID().toString());
        original.setType(UUID.randomUUID().toString());
        original.setFormat(UUID.randomUUID().toString());
    }

    @Override
    public AbstractModelService<Definition> getModelServiceImpl() {
        return definitionService;
    }

}
