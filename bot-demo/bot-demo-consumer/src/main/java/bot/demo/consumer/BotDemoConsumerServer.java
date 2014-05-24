package bot.demo.consumer;

import bot.demo.messages.ScanReply;
import bot.demo.messages.Topics;
import com.google.common.primitives.Ints;
import machine.lib.message.HandlingMessageService;
import machine.lib.message.NetworkMessageListener;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.management.api.services.NetworkService;
import machine.management.api.services.RemoteManagementService;
import machine.message.api.entities.NetworkMessage;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class BotDemoConsumerServer {

    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());
    private static final Logger LOG = LoggerFactory.getLogger(BotDemoConsumerServer.class);
    private final BotDemoConsumerConfiguration configuration;

    public BotDemoConsumerServer(BotDemoConsumerConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws EmbeddedServerStartException {

        String backboneUrl = System.getProperty("BACKBONE_URL", "http://localhost:82/");
        Integer serverPort = Ints.tryParse(System.getProperty("SERVER_PORT", "8081"));

        LOG.info("BACKBONE_URL: {}", backboneUrl);
        LOG.info("SERVER_PORT: {}", serverPort);

        BotDemoConsumerConfiguration configuration = new BotDemoConsumerConfiguration();
        configuration.setBackboneUrl(backboneUrl);
        configuration.setServerPort(serverPort);

        BotDemoConsumerServer server = new BotDemoConsumerServer(configuration);
        server.start();

    }

    public void start() throws EmbeddedServerStartException {

        RemoteManagementService remoteManagementService = JAXRSClientFactory.create(configuration.getBackboneUrl(), RemoteManagementService.class, PROVIDERS);
        String managementURL = remoteManagementService.getManagementURL();
        final UUID serverId = remoteManagementService.getServerId();
        final NetworkService networkService = JAXRSClientFactory.create(managementURL, NetworkService.class, PROVIDERS);
        HandlingMessageService handlingMessageService = new HandlingMessageService(configuration.getServerPort(), networkService);

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(handlingMessageService);
        embeddedServer.start(services);

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
