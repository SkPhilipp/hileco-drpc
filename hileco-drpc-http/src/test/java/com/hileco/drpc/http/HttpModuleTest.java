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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpModuleTest {

    private static final Logger LOG = LoggerFactory.getLogger(HttpModuleTest.class);

    public interface SampleService {

        public Integer calculate(Integer a, Integer b);

    }

    @Test
    public void test() throws Exception {

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

        LOG.info("Starting.");

        Subscription save = proxyServiceHost.connector(SubscriptionService.class).connect(HttpRouter.ROUTER_IDENTIFIER).save("fsdadsfdfs", "ssfadsdfsdaf", 124234);

        LOG.info("Done, id = {}.", save.getId());

    }

}
