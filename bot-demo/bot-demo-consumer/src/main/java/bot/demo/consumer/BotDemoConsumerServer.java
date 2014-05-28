package bot.demo.consumer;

import bot.demo.consumer.api.ConsumerImpl;
import machine.lib.message.DelegatingMessageService;
import machine.lib.message.proxy.RemoteProxyBuilder;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
import machine.management.api.services.NetworkService;
import machine.management.api.services.RemoteManagementService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import java.util.*;

public class BotDemoConsumerServer implements LocalServer {

    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());
    private final BotDemoConsumerConfiguration configuration;

    public BotDemoConsumerServer(BotDemoConsumerConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws EmbeddedServerStartException {
        BotDemoConsumerConfiguration configuration = new BotDemoConsumerConfiguration();
        Config.set("BACKBONE_URL", "http://localhost:82/", configuration::setBackboneUrl);
        Config.set("SERVER_PORT", 8081, configuration::setServerPort);
        BotDemoConsumerServer server = new BotDemoConsumerServer(configuration);
        server.start();
    }

    public void start() throws EmbeddedServerStartException {

        RemoteManagementService remoteManagementService = JAXRSClientFactory.create(configuration.getBackboneUrl(), RemoteManagementService.class, PROVIDERS);
        String managementURL = remoteManagementService.getManagementURL();
        UUID serverId = remoteManagementService.getServerId();
        NetworkService networkService = JAXRSClientFactory.create(managementURL, NetworkService.class, PROVIDERS);
        DelegatingMessageService delegatingMessageService = new DelegatingMessageService(configuration.getServerPort(), networkService);
        RemoteProxyBuilder remoteProxyBuilder = new RemoteProxyBuilder(delegatingMessageService);

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(delegatingMessageService);
        embeddedServer.start(services);

        ConsumerImpl consumerImpl = new ConsumerImpl(serverId, remoteProxyBuilder);
        consumerImpl.start();

    }

}
