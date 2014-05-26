package bot.demo.consumer;

import bot.demo.consumer.handlers.ProcessActionHandler;
import com.google.common.primitives.Ints;
import machine.lib.message.DelegatingMessageService;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.management.api.services.NetworkService;
import machine.management.api.services.RemoteManagementService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BotDemoConsumerServer implements LocalServer {

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
        UUID serverId = remoteManagementService.getServerId();
        NetworkService networkService = JAXRSClientFactory.create(managementURL, NetworkService.class, PROVIDERS);
        DelegatingMessageService delegatingMessageService = new DelegatingMessageService(configuration.getServerPort(), networkService);

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(delegatingMessageService);
        embeddedServer.start(services);

        ProcessActionHandler processActionHandler = new ProcessActionHandler(serverId, delegatingMessageService);
        processActionHandler.start();

    }

}
