package machine.router;

import com.google.common.primitives.Ints;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
import machine.router.processes.SubscriptionCleaner;
import machine.router.services.RouterServiceImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RouterServer implements LocalServer {

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private RouterConfiguration configuration;

    public RouterServer(RouterConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws Exception {
        RouterConfiguration configuration = new RouterConfiguration();
        Config.set("SERVER_PORT", 80, configuration::setServerPort, Ints::tryParse);
        RouterServer routerServer = new RouterServer(configuration);
        routerServer.start();

    }

    public void start() throws EmbeddedServerStartException {

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(new RouterServiceImpl());
        embeddedServer.start(services);

        SubscriptionCleaner subscriptionCleaner = new SubscriptionCleaner();
        scheduledExecutorService.scheduleAtFixedRate(subscriptionCleaner, 2, 2, TimeUnit.SECONDS);

    }

}
