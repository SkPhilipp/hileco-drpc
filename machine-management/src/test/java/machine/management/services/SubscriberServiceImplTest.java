package machine.management.services;

import machine.management.model.Subscriber;
import machine.management.services.generic.AbstractModelService;
import machine.management.services.helpers.AbstractModelServiceTester;
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
    public void randomize(Subscriber original) {
        original.setTarget(UUID.randomUUID().toString());
        original.setTopic(UUID.randomUUID().toString());
    }

    @Override
    public AbstractModelService<Subscriber> getModelServiceImpl() {
        return subscriberService;
    }

}
