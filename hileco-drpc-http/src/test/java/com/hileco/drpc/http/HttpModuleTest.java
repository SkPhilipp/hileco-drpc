package com.hileco.drpc.http;

import com.hileco.drpc.core.ProxyServiceHost;
import com.hileco.drpc.http.core.HttpMessageSender;
import com.hileco.drpc.http.routing.HttpRouter;
import com.hileco.drpc.http.routing.services.SubscriptionService;
import com.hileco.drpc.http.routing.services.subscriptions.CacheSubscriptionStore;
import com.hileco.drpc.http.routing.services.subscriptions.Subscription;
import com.hileco.drpc.http.routing.services.subscriptions.SubscriptionStore;
import com.hileco.drpc.http.server.MessageReceiverServer;
import com.hileco.drpc.http.server.RouterServer;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
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
     * More or less of a benchmark for client to router communication on the same local machine ( meaning without network overhead ).
     * <p/>
     * Results of 10 threads each making 25_000 calls to the Router-owned {@link SubscriptionService}:
     * - Time passed = 42266ms
     * - Per call = 0.169064ms
     * <p/>
     * Approximately 5800 calls per second, where one call is a service method invocation:
     * - C: An invocation is made:
     * - C: Serializing arguments to JSON
     * - C: Sending message metadata and JSON over HTTP from client to router
     * - R: Parsing received metadata at router
     * - R: Deserializing the JSON, after seeing the metadata indicates the message is meant for a service hosted by the router
     * - R: Invoking the service with the deserialized arguments
     * - R: Serializing the result of the service method invocation
     * - R: Sending callback message metadata and JSON over HTTP from router to client
     * - C: Deserializing the JSON, after seeing the metadata indicates the callback message is meant for a registered callback handler
     * - C: Returning the object to the invoker.
     */
    @Ignore
    @Test
    public void testClientToRouterCalls() throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> LOG.error("A thread erred.", ex));

        // initiate a router on port 8080
        SubscriptionStore subscriptionStore = new CacheSubscriptionStore();
        RouterServer routerServer = new RouterServer(subscriptionStore);
        routerServer.start(8080);

        // create a sender pointing to that router
        HttpClient httpClient = HttpClients.createDefault();
        HttpMessageSender httpMessageSender = new HttpMessageSender(httpClient, HttpRouter.DEFAULT_STREAMER, "http://localhost:8080", "localhost", 8081);
        ProxyServiceHost proxyServiceHost = new ProxyServiceHost(httpMessageSender, HttpRouter.DEFAULT_STREAMER);
        MessageReceiverServer messageReceiverServer = new MessageReceiverServer();
        messageReceiverServer.start(8081, proxyServiceHost);

        long now = System.currentTimeMillis();
        int iters = 25000;
        int threads = 10;

        SubscriptionService subscriptionService = proxyServiceHost.connector(SubscriptionService.class).connect(HttpRouter.ROUTER_IDENTIFIER);

        List<Thread> threadList = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < iters; i++) {
                    Subscription save = subscriptionService.save("sample data", "sample data", 1234);
                    Assert.assertNotNull(save.getId());
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
