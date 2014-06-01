package bot.demo.consumer;

import bot.demo.consumer.api.ConsumerServiceImpl;
import com.google.common.primitives.Ints;
import machine.lib.message.DelegatingMessageService;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
import machine.router.api.services.RouterService;
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
        Config.set("MANAGEMENT_URL", "http://localhost:82/", configuration::setRouterURL);
        Config.set("SERVER_ID", UUID.randomUUID(), configuration::setServerId, UUID::fromString);
        Config.set("SERVER_PORT", 8081, configuration::setServerPort, Ints::tryParse);
        BotDemoConsumerServer server = new BotDemoConsumerServer(configuration);
        server.start();
    }

    public void start() throws EmbeddedServerStartException {

        String managementURL = configuration.getRouterURL();
        RouterService routerService = JAXRSClientFactory.create(managementURL, RouterService.class, PROVIDERS);
        DelegatingMessageService delegatingMessageService = new DelegatingMessageService(configuration.getServerPort(), routerService);

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(delegatingMessageService);
        embeddedServer.start(services);

        ConsumerServiceImpl consumerImpl = new ConsumerServiceImpl(configuration.getServerId(), delegatingMessageService);
        consumerImpl.start();

    }

}
