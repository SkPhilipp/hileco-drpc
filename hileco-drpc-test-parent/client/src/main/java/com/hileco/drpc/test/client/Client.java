package com.hileco.drpc.test.client;

import com.hileco.drpc.test.client.api.MasterServiceImpl;
import machine.drcp.http.api.models.HTTPSubscription;
import machine.drcp.http.impl.Router;
import machine.drcp.http.impl.RouterClient;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;

import java.util.HashSet;
import java.util.Set;

public class Client {

    public void start(Integer port) throws EmbeddedServerStartException {

        Router router = new Router();

        RouterClient routerClient = new RouterClient(router, () -> {
            HTTPSubscription subscription = new HTTPSubscription();
            subscription.setPort(port);
            return subscription;
        });

        EmbeddedServer embeddedServer = new EmbeddedServer(port);
        Set<Object> services = new HashSet<>();
        services.add(router);
        services.add(routerClient);
        embeddedServer.start(services);

        MasterServiceImpl remoteMaster = new MasterServiceImpl(routerClient.getClient());
        remoteMaster.start();

    }

}
