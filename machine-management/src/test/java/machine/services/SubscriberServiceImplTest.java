package machine.services;

import machine.management.model.Subscriber;
import machine.services.helpers.AbstractModelServiceTester;
import machine.services.lib.services.AbstractModelService;
import org.junit.Assert;

import java.util.UUID;

public class SubscriberServiceImplTest extends AbstractModelServiceTester<Subscriber> {

    private SubscriberServiceImpl subscriberService = new SubscriberServiceImpl();

    @Override
    public void assertEquals(Subscriber expected, Subscriber actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getTarget(), actual.getTarget());
        Assert.assertEquals(expected.getTopic(), actual.getTopic());
    }

    @Override
    public void randomizeModel(Subscriber original) {
        original.setTarget(UUID.randomUUID().toString());
        original.setTopic(UUID.randomUUID().toString());
    }

    @Override
    public AbstractModelService<Subscriber> getModelServiceImpl() {
        return subscriberService;
    }

}
