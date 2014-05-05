package machine.management.services;

import machine.management.domain.Message;
import machine.management.domain.TaskStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static machine.management.services.utils.Randoms.*;

@Ignore
public class MessageServiceImplTest {

    private MessageServiceImpl service = new MessageServiceImpl();

    /**
     * Create an instance, reads it out and asserts:
     * - missing field id is now filled
     * - missing field timestamp is now filled
     */
    @Test
    public void testCreateRead() throws Exception {
        TaskStatus taskStatus = randomEnum(TaskStatus.class);
        Message instance = new Message();
        instance.setTopic(randomString());
        instance.setContent(randomBytes());
        service.create(instance);
        Message readInstance = service.read(instance);
        Assert.assertNotNull(readInstance.getId());
        Assert.assertNotNull(readInstance.getTimestamp());
    }

}
