package machine.management.services;

import machine.management.domain.Event;
import machine.management.services.helpers.AbstractModelServiceTester;
import machine.management.services.lib.services.AbstractModelService;
import org.junit.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class EventServiceImplTest extends AbstractModelServiceTester<Event> {

    private EventServiceImpl eventService = new EventServiceImpl();

    @Override
    public void assertEquals(Event expected, Event actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertArrayEquals(expected.getContent(), actual.getContent());
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        Assert.assertEquals(expected.getTopic(), actual.getTopic());
    }

    @Override
    public void randomizeModel(Event original) {
        original.setTopic(UUID.randomUUID().toString());
        Date now = Calendar.getInstance().getTime();
        original.setTimestamp(now.getTime());
        byte[] bytes = new byte[100];
        Random random = new Random();
        random.nextBytes(bytes);
        original.setContent(bytes);
    }

    @Override
    public AbstractModelService<Event> getModelServiceImpl() {
        return eventService;
    }

}
