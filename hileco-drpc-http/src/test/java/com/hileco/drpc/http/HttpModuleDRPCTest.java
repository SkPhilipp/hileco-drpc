package com.hileco.drpc.http;

import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.http.router.subscription.CacheSubscriptionStore;
import com.hileco.drpc.http.router.RouterServer;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Philipp Gayret
 */
public class HttpModuleDRPCTest {

    private static final Logger LOG = LoggerFactory.getLogger(HttpModuleDRPCTest.class);

    public static final int DRPC_INTERVAL_MS = 100;
    public static final int PORT_BASE_RANGE_SERVICES = 8300;
    public static final int PORT_BASE_RANGE_CLIENTS = 8400;
    public static final int PORT_ROUTER = 8500;
    public static final String ROUTER_URL = "http://localhost:" + PORT_ROUTER;
    public static final String HOST = "localhost";

    public static interface SampleService {

        public Integer calculate(Integer a, Integer b);

    }

    @Ignore
    @Test
    public void startAll() throws Exception {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(100);

        CacheSubscriptionStore subscriptionStore = new CacheSubscriptionStore();
        RouterServer routerServer = new RouterServer(subscriptionStore);
        routerServer.start(PORT_ROUTER);

        LOG.info("Spawning services");

        for (int index = 0; index < 10; index++) {
            Client client = new Client(ROUTER_URL, HOST, PORT_BASE_RANGE_SERVICES + index);
            client.publish(SampleService.class, Integer.toString(index), (a, b) -> a + b);
        }

        LOG.info("Spawning clients");

        for (int index = 0; index < 1; index++) {
            Client client = new Client(ROUTER_URL, HOST, PORT_BASE_RANGE_CLIENTS + index);
            scheduler.scheduleAtFixedRate(() -> {
                ServiceConnector<SampleService> connector = client.connector(SampleService.class);
                connector.drpc(d -> d.calculate(1, 2), r -> LOG.info("Obtained a result: {}", r));
            }, DRPC_INTERVAL_MS, DRPC_INTERVAL_MS, TimeUnit.MILLISECONDS);
        }

        LOG.info("Sleeping");

        Thread.sleep(10000);

    }

}
