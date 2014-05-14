package machine.management;

import com.google.common.primitives.Ints;
import machine.lib.service.EmbeddedServer;
import machine.management.processes.SubscriptionCleaner;
import machine.management.services.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "80"));
    public static final  ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws Exception {
        // start up server
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(new DefinitionServiceImpl());
        services.add(new ServerServiceImpl());
        services.add(new NetworkServiceImpl());
        services.add(new TaskServiceImpl());
        embeddedServer.start(services);
        // schedule processes
        SubscriptionCleaner subscriptionCleaner = new SubscriptionCleaner();
        scheduledExecutorService.scheduleWithFixedDelay(subscriptionCleaner, 0, 5000, TimeUnit.SECONDS);
    }

}
