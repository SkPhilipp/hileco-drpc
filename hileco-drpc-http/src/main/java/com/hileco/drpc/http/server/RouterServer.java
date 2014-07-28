package com.hileco.drpc.http.server;

import com.hileco.drpc.http.routing.HttpRequestHandlerRouterAdapter;
import com.hileco.drpc.http.routing.HttpRouter;
import com.hileco.drpc.http.routing.services.SubscriptionService;
import com.hileco.drpc.http.routing.services.SubscriptionStoreServiceAdapter;
import com.hileco.drpc.http.routing.services.subscriptions.SubscriptionStore;
import com.hileco.drpc.http.server.grizzly.GrizzlyServer;

public class RouterServer {

    private final GrizzlyServer grizzlyServer;
    private final SubscriptionStore subscriptionStore;

    public RouterServer(SubscriptionStore subscriptionStore) {
        this.subscriptionStore = subscriptionStore;
        this.grizzlyServer = new GrizzlyServer();
    }

    public void start(Integer port) throws Exception {

        // initialize router internal subscription service, along with an http router
        SubscriptionService subscriptionService = new SubscriptionStoreServiceAdapter(subscriptionStore);
        HttpRouter httpRouter = new HttpRouter(subscriptionStore);
        httpRouter.getRouterServiceHost().registerService(SubscriptionService.class, HttpRouter.ROUTER_IDENTIFIER, subscriptionService);

        // initialize a servlet to the router and let it handle all requests
        HttpRequestHandlerRouterAdapter httpRequestHandlerRouterAdapter = new HttpRequestHandlerRouterAdapter(httpRouter);
        this.grizzlyServer.start(port, httpRequestHandlerRouterAdapter);

    }

}
