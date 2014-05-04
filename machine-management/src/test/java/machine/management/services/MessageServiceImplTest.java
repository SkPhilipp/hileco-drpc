package machine.management.services;

import machine.management.domain.Message;
import machine.management.services.helpers.AbstractModelServiceTester;
import machine.management.services.lib.services.AbstractModelService;
import org.junit.Assert;

import java.util.Random;
import java.util.UUID;

public class MessageServiceImplTest extends AbstractModelServiceTester<Message> {

    private MessageServiceImpl eventService = new MessageServiceImpl();

    @Override
    public void assertEquals(Message expected, Message actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertArrayEquals(expected.getContent(), actual.getContent());
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        Assert.assertEquals(expected.getTopic(), actual.getTopic());
    }

    @Override
    public void randomizeModel(Message original) {
        original.setTopic(UUID.randomUUID().toString());
        byte[] bytes = new byte[100];
        Random random = new Random();
        random.nextBytes(bytes);
        original.setContent(bytes);
    }

    @Override
    public AbstractModelService<Message> getModelServiceImpl() {
        return eventService;
    }

}
