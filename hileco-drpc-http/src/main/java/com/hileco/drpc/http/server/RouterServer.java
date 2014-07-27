package com.hileco.drpc.http.server;

import com.hileco.drpc.http.routing.HttpRouter;
import com.hileco.drpc.http.routing.HttpRouterServletAdapter;
import com.hileco.drpc.http.routing.services.SubscriptionService;
import com.hileco.drpc.http.routing.services.SubscriptionStoreServiceAdapter;
import com.hileco.drpc.http.routing.services.subscriptions.SubscriptionStore;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

public class RouterServer {

    private final BasicServer basicServer;
    private final SubscriptionStore subscriptionStore;

    public RouterServer(SubscriptionStore subscriptionStore) {
        this.subscriptionStore = subscriptionStore;
        this.basicServer = new BasicServer();
    }

    public void start(Integer port) throws Exception {

        // initialize router internal subscription service, along with an http router
        SubscriptionService subscriptionService = new SubscriptionStoreServiceAdapter(subscriptionStore);
        HttpRouter httpRouter = new HttpRouter(subscriptionStore);
        httpRouter.getRouterServiceHost().registerService(SubscriptionService.class, HttpRouter.ROUTER_IDENTIFIER, subscriptionService);

        // initialize a servlet to the router and let it handle all requests
        HttpServlet httpRouterServlet = new HttpRouterServletAdapter(httpRouter);
        Map<String, Servlet> servlets = new HashMap<>();
        servlets.put("/*", httpRouterServlet);
        this.basicServer.start(port, servlets);

    }

}
