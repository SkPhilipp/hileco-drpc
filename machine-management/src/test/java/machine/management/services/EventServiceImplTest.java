package machine.management.services;

import machine.management.model.Event;
import machine.management.services.helpers.AbstractQueryableModelServiceTester;
import machine.management.services.lib.services.AbstractQueryableModelService;
import org.junit.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class EventServiceImplTest extends AbstractQueryableModelServiceTester<Event> {

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
        // TODO: Test helpers *must* be split up before any new tests, as we are now missing coverage because of the inheritance structure
        // original.setContent(UUID.randomUUID().toString().getBytes(Charsets.UTF_8));
        original.setTopic(UUID.randomUUID().toString());
        Date now = Calendar.getInstance().getTime();
        original.setTimestamp(now.getTime());
    }

    @Override
    public AbstractQueryableModelService<Event> getQueryableModelServiceImpl() {
        return eventService;
    }

}
