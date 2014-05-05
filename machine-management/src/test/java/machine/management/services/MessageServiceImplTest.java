package machine.management.services;

import machine.lib.client.messaging.NetworkMessage;
import machine.lib.service.dao.GenericModelDAO;
import machine.management.domain.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

import static machine.management.services.utils.Randoms.randomBytes;
import static machine.management.services.utils.Randoms.randomString;

public class MessageServiceImplTest {

    private MessageServiceImpl service = new MessageServiceImpl();
    private static final GenericModelDAO<Message> messageDAO = new GenericModelDAO<>(Message.class);

    /**
     * Create an instance, reads it out and asserts:
     * - missing field id is now filled
     * - missing field timestamp is now filled
     */
    @Test
    public void testCreateRead() throws Exception {
        NetworkMessage<byte[]> instance = new NetworkMessage<>(randomString(), randomBytes());
        UUID messageId = service.publish(instance);
        Message readInstance = messageDAO.read(messageId);
        Assert.assertNotNull(readInstance.getId());
        Assert.assertNotNull(readInstance.getTimestamp());
    }

}
