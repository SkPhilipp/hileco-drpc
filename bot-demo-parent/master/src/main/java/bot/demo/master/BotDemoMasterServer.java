package bot.demo.master;

import bot.demo.master.api.MasterServiceImpl;
import com.google.common.primitives.Ints;
import machine.drcp.http.api.models.HTTPSubscription;
import machine.drcp.http.impl.RouterClient;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;

import java.util.HashSet;
import java.util.Set;

public class BotDemoMasterServer implements LocalServer {

    private final BotDemoMasterConfiguration configuration;

    public BotDemoMasterServer(BotDemoMasterConfiguration configuration) throws EmbeddedServerStartException {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws Exception {
        BotDemoMasterConfiguration configuration = new BotDemoMasterConfiguration();
        Config.set("ROUTER_URL", "http://localhost:80/", configuration::setRouterUrl);
        Config.set("HUMANITY_SOURCE", "v", configuration::setHumanitySource);
        Config.set("SERVER_PORT", 8080, configuration::setServerPort, Ints::tryParse);
        BotDemoMasterServer server = new BotDemoMasterServer(configuration);
        server.start();
    }

    public void start() throws EmbeddedServerStartException {

        RouterClient RouterClient = new RouterClient(() -> {
            HTTPSubscription subscription = new HTTPSubscription();
            subscription.setPort(configuration.getServerPort());
            return subscription;
        }, configuration.getRouterUrl());

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(RouterClient);
        embeddedServer.start(services);

        MasterServiceImpl remoteMaster = new MasterServiceImpl(RouterClient.getClient());
        remoteMaster.start();

    }

}
