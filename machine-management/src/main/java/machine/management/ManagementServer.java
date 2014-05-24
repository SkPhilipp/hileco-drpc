package machine.management;

import com.google.common.primitives.Ints;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.management.processes.SubscriptionCleaner;
import machine.management.services.DefinitionServiceImpl;
import machine.management.services.NetworkServiceImpl;
import machine.management.services.ServerServiceImpl;
import machine.management.services.TaskServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ManagementServer {

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final Logger LOG = LoggerFactory.getLogger(ManagementServer.class);
    private ManagementConfiguration configuration;

    public ManagementServer(ManagementConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws Exception {

        Integer serverPort = Ints.tryParse(System.getProperty("SERVER_PORT", "80"));

        LOG.info("SERVER_PORT: {}", serverPort);

        ManagementConfiguration configuration = new ManagementConfiguration();
        configuration.setServerPort(serverPort);

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
        scheduledExecutorService.scheduleAtFixedRate(subscriptionCleaner, 0, 5, TimeUnit.SECONDS);

    }

}
