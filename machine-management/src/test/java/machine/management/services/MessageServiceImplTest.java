package machine.management.services;

import machine.management.domain.Message;
import org.junit.Assert;
import org.junit.Test;

import static machine.management.services.utils.Randoms.randomBytes;
import static machine.management.services.utils.Randoms.randomString;

public class MessageServiceImplTest {

    private MessageServiceImpl service = new MessageServiceImpl();

    /**
     * Create an instance, reads it out and asserts:
     * - missing field id is now filled
     * - missing field timestamp is now filled
     */
    @Test
    public void testCreateRead() throws Exception {
        //
        Message instance = new Message();
        instance.setTopic(randomString());
        instance.setContent(randomBytes());
        service.create(instance);
        Message readInstance = service.read(instance);
        Assert.assertNotNull(readInstance.getId());
        Assert.assertNotNull(readInstance.getTimestamp());
    }

}
