package com.hileco.drpc.http;

import com.hileco.drpc.http.subscription.CacheSubscriptionStore;
import com.hileco.drpc.http.subscription.SubscriptionStore;
import com.hileco.drpc.http.router.HttpRouter;
import com.hileco.drpc.http.router.RouterServer;
import com.hileco.drpc.http.router.services.PingService;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philipp Gayret
 */
public class HttpModuleTest {

    private static final Logger LOG = LoggerFactory.getLogger(HttpModuleTest.class);

    /**
     * This test measures the per-message overhead of using HTTP.
     * <p/>
     * Results of 10 threads each making 25_000 calls to the Router-owned {@link PingService}:
     * - Time passed = 38378ms
     * - Per call = 0.153512ms
     * <p/>
     * Approximately 390_000 calls per minute, where one call is a service method invocation:
     * - C: An invocation is made:
     * - C: Serializing null-arguments to JSON
     * - C: Sending message metadata and JSON over HTTP from client to router
     * - R: Parsing received metadata at router
     * - R: Deserializing the empty JSON array, after seeing the metadata indicates the message is meant for a service hosted by the router
     * - R: Invoking the service with the deserialized arguments
     * - R: Serializing the true-result of the service method invocation
     * - R: Sending callback message metadata and JSON over HTTP from router to client
     * - C: Deserializing the JSON array containing true, after seeing the metadata indicates the callback message is meant for a registered callback handler
     * - C: Returning the object to the invoker.
     */
    @Ignore
    @Test
    public void benchmarkLocalPing() throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> LOG.error("A thread erred.", ex));

        // initiate a router on port 8080
        SubscriptionStore subscriptionStore = new CacheSubscriptionStore();
        RouterServer routerServer = new RouterServer(subscriptionStore);
        routerServer.start(8080);

        // create a sender pointing to that router
        Client client = new Client("http://localhost:8080", "localhost", 8081);

        long now = System.currentTimeMillis();
        int iters = 25000;
        int threads = 10;

        PingService pingService = client.connector(PingService.class).connect(HttpRouter.ROUTER_IDENTIFIER);

        List<Thread> threadList = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < iters; i++) {
                    pingService.ping();
                }
            });
            thread.start();
            threadList.add(thread);
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        long end = System.currentTimeMillis();

        LOG.info("Time passed = {}ms", end - now);
        LOG.info("Per call = {}ms", ((end - now) * 1f) / (iters * threads));

    }

}
