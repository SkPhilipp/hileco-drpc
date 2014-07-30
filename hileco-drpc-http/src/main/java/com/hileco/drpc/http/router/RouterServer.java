package com.hileco.drpc.http.router;

import com.hileco.drpc.http.subscription.SubscriptionStore;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.core.HttpHeaderUtils;
import com.hileco.drpc.http.core.grizzly.GrizzlyServer;
import com.hileco.drpc.http.router.services.PingService;
import com.hileco.drpc.http.router.services.PingServiceImpl;
import com.hileco.drpc.http.router.services.SubscriptionService;
import com.hileco.drpc.http.router.services.SubscriptionServiceImpl;

public class RouterServer {

    private final GrizzlyServer grizzlyServer;
    private final SubscriptionStore subscriptionStore;

    public RouterServer(SubscriptionStore subscriptionStore) {
        this.subscriptionStore = subscriptionStore;
        this.grizzlyServer = new GrizzlyServer();
    }

    public void start(Integer port) throws Exception {

        // initialize router internal services
        SubscriptionService subscriptionService = new SubscriptionServiceImpl(subscriptionStore);
        PingService pingService = new PingServiceImpl();

        // initialize router itself, and register the services
        HttpRouter httpRouter = new HttpRouter(subscriptionStore);
        httpRouter.registerRouterService(SubscriptionService.class, subscriptionService);
        httpRouter.registerRouterService(PingService.class, pingService);

        // initialize with a servlet to forward all requests to the router
        this.grizzlyServer.start(port, (httpRequest) -> {
            String replyToHost = HttpHeaderUtils.getReplyToHost(httpRequest);
            Integer replyToPost = HttpHeaderUtils.getReplyToPost(httpRequest);
            Metadata metadata = HttpHeaderUtils.fromHeaders(httpRequest::getHeader);
            httpRouter.accept(replyToHost, replyToPost, metadata, httpRequest.getInputStream());
        });

    }

}
