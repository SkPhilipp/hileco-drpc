package bot.demo.consumer;

import bot.demo.messages.ScanReply;
import bot.demo.messages.Topics;
import com.google.common.primitives.Ints;
import machine.lib.message.HandlingMessageService;
import machine.lib.message.NetworkMessageListener;
import machine.lib.service.EmbeddedServer;
import machine.management.api.services.NetworkService;
import machine.management.api.services.RemoteManagementService;
import machine.message.api.entities.NetworkMessage;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class Main {

    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());

    public static final String BACKBONE_URL = System.getProperty("BACKBONE_URL", "http://localhost:82/");
    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "8081"));
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        // log environment
        LOG.info("Starting with:");
        LOG.info("- BACKBONE_URL: {}", BACKBONE_URL);
        LOG.info("- SERVER_PORT: {}", SERVER_PORT);
        // set up services
        RemoteManagementService remoteManagementService = JAXRSClientFactory.create(BACKBONE_URL, RemoteManagementService.class, PROVIDERS);
        String managementURL = remoteManagementService.getManagementURL();
        final UUID serverId = remoteManagementService.getServerId();
        final NetworkService networkService = JAXRSClientFactory.create(managementURL, NetworkService.class, PROVIDERS);
        HandlingMessageService handlingMessageService = new HandlingMessageService(SERVER_PORT, networkService);
        // run the server
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(handlingMessageService);
        embeddedServer.start(services);
        // do a sample callback
        handlingMessageService.beginListen(Topics.SCAN, new NetworkMessageListener<Serializable>() {
            @Override
            public void handle(NetworkMessage<?> message) {
                ScanReply scanReply = new ScanReply();
                scanReply.setServerId(serverId);
                NetworkMessage<?> reply = new NetworkMessage<>(Topics.SCAN_REPLY, scanReply);
                networkService.publish(reply);
            }
        });
    }

}
