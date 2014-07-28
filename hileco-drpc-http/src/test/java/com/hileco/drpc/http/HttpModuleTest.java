package com.hileco.drpc.http;

import com.hileco.drpc.core.ProxyServiceHost;
import com.hileco.drpc.http.core.HttpConstants;
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

/**
 * @author Philipp Gayret
 */
public class HttpModuleTest {

    private static final Logger LOG = LoggerFactory.getLogger(HttpModuleTest.class);

    /**
     * More or less of a benchmark for client - router communication.
     * <p/>
     * With 500,000 iterations of calling the Router-owned {@link SubscriptionService}, the results were:
     * <p/>
     * - 20:45:22,045  INFO main HttpModuleTest:52 - Time passed = 134609ms
     * - 20:45:22,049  INFO main HttpModuleTest:53 - Per call = 0.269218ms
     * <p/>
     * Approximately 0.27ms per second ( =~ 4 messages per 1ms ).
     * <p/>
     * Performing more requests will usually result in faster averages.
     *
     * @throws Exception
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
        HttpMessageSender httpMessageSender = new HttpMessageSender(httpClient, HttpConstants.DEFAULT_STREAMER, "http://localhost:8080", "localhost", 8081);
        ProxyServiceHost proxyServiceHost = new ProxyServiceHost(httpMessageSender, HttpConstants.DEFAULT_STREAMER);
        MessageReceiverServer messageReceiverServer = new MessageReceiverServer();
        messageReceiverServer.start(8081, proxyServiceHost);

        long now = System.currentTimeMillis();
        int iters = 5000;

        SubscriptionService subscriptionService = proxyServiceHost.connector(SubscriptionService.class).connect(HttpRouter.ROUTER_IDENTIFIER);

        for (int i = 0; i < iters; i++) {
            Subscription save = subscriptionService.save("sample data", "sample data", 1234);
            Assert.assertNotNull(save.getId());
        }

        long end = System.currentTimeMillis();
        LOG.info("Time passed = {}ms", end - now);
        LOG.info("Per call = {}ms", ((end - now) * 1f) / iters);

    }

}
