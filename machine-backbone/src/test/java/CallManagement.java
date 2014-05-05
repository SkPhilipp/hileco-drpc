import machine.lib.client.messaging.NetworkMessage;
import machine.lib.client.messaging.NetworkMessageRouter;
import org.junit.Test;

public class CallManagement {

    @Test
    public void go() throws Exception {
        for (int i = 0; i < 10; i++) {
            NetworkMessage<String> stringNetworkMessage = new NetworkMessage<>("hello", "hello world");
            NetworkMessageRouter networkMessageRouter = new NetworkMessageRouter();
            networkMessageRouter.submit("http://localhost:8080/services/messages/publish", stringNetworkMessage);
        }
    }

}
