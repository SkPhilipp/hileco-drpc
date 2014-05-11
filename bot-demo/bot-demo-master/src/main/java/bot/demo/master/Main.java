package bot.demo.master;

import bot.demo.messages.Topics;
import com.google.common.primitives.Ints;
import machine.lib.message.CallbackHandler;
import machine.lib.message.CallbackMessageService;
import machine.lib.service.EmbeddedServer;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static final String MANAGEMENT_URL = System.getProperty("MANAGEMENT_URL", "http://localhost:80/");
    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "8080"));
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        // log environment
        LOG.info("Starting with:");
        LOG.info("- MANAGEMENT_URL: {}", MANAGEMENT_URL);
        LOG.info("- SERVER_PORT: {}", SERVER_PORT);
        // set up services
        NetworkService networkService = JAXRSClientFactory.create(MANAGEMENT_URL, NetworkService.class);
        CallbackMessageService callbackMessageService = new CallbackMessageService(SERVER_PORT, networkService);
        // run the server
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(callbackMessageService);
        embeddedServer.start(services);
        // do a sample callback
        callbackMessageService.beginCallback(Topics.SCAN, "", new CallbackHandler() {
            @Override
            public void handle(NetworkMessage<?> message) {
                LOG.info("Whoo! Received a message with topic {} and id {}", message.getTopic(), message.getMessageId());
            }
        });
    }

}
