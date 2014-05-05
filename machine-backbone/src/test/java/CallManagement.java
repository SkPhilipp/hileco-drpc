import machine.lib.client.messaging.NetworkMessage;
import machine.management.api.services.MessageService;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.BeforeClass;
import org.junit.Test;

public class CallManagement {

    public static final String MANAGEMENT_URL = "http://localhost:8080/services/";
    private static MessageService messageService;

    @BeforeClass
    public static void beforeClass() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(MANAGEMENT_URL);
        messageService = target.proxy(MessageService.class);
    }

    @Test
    public void go() throws Exception {
        for (int i = 0; i < 10; i++) {
            NetworkMessage<String> stringNetworkMessage = new NetworkMessage<>("hello", "hello world");
            messageService.publish(stringNetworkMessage);
        }
    }

}
