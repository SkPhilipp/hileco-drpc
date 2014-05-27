package machine.management;

import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
import machine.management.processes.SubscriptionCleaner;
import machine.management.services.DefinitionServiceImpl;
import machine.management.services.NetworkServiceImpl;
import machine.management.services.ServerServiceImpl;
import machine.management.services.TaskServiceImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ManagementServer implements LocalServer {

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ManagementConfiguration configuration;

    public ManagementServer(ManagementConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws Exception {
        ManagementConfiguration configuration = new ManagementConfiguration();
        Config.set("SERVER_PORT", 80, configuration::setServerPort);
        ManagementServer managementServer = new ManagementServer(configuration);
        managementServer.start();

    }

    public void start() throws EmbeddedServerStartException {

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(new DefinitionServiceImpl());
        services.add(new ServerServiceImpl());
        services.add(new NetworkServiceImpl());
        services.add(new TaskServiceImpl());
        embeddedServer.start(services);

        SubscriptionCleaner subscriptionCleaner = new SubscriptionCleaner();
        scheduledExecutorService.scheduleAtFixedRate(subscriptionCleaner, 2, 2, TimeUnit.SECONDS);

    }

}
