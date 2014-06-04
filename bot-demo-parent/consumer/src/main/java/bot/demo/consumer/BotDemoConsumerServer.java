package bot.demo.consumer;

import bot.demo.consumer.live.Process;
import com.google.common.primitives.Ints;
import machine.drcp.http.api.models.HTTPSubscription;
import machine.drcp.http.impl.RouterClient;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
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
        RouterClient RouterClient = new RouterClient(() -> {
            HTTPSubscription subscription = new HTTPSubscription();
            subscription.setPort(configuration.getServerPort());
            return subscription;
        }, managementURL);

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(RouterClient);
        embeddedServer.start(services);

        Process consumerImpl = new bot.demo.consumer.live.Process(configuration.getServerId(), RouterClient.getClient());
        consumerImpl.start();

    }

}
