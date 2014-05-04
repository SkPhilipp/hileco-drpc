package machine.management.services;

import machine.management.domain.Message;
import org.junit.Assert;
import org.junit.Test;

import static machine.management.services.utils.Randoms.randomBytes;
import static machine.management.services.utils.Randoms.randomString;

public class MessageServiceImplTest {

    private MessageServiceImpl eventService = new MessageServiceImpl();

    @Test
    public void testCreateRead() throws Exception {

        // create a message
        Message message = new Message();
        message.setTopic(randomString());
        message.setContent(randomBytes());
        eventService.create(message);

        // read it out by id and assert missing fields are now filled
        Message readMessage = eventService.read(message);
        Assert.assertNotNull(readMessage);
        Assert.assertNotNull(readMessage.getId());
        Assert.assertNotNull(readMessage.getTimestamp());

    }

}
