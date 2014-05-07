package machine.backbone;

import machine.management.api.entities.NetworkMessage;
import machine.management.api.services.MessageService;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ManagementTest {

    /**
     * Publishes some messages on the topic "hello".
     */
    @Test
    public void testPublishMessages() throws Exception {
        Management management = Management.getInstance();
        MessageService messageService = management.getMessageService();
        for (int i = 0; i < 10; i++) {
            NetworkMessage<String> stringNetworkMessage = new NetworkMessage<>("hello", "hello world");
            messageService.publish(stringNetworkMessage);
        }
    }

}
