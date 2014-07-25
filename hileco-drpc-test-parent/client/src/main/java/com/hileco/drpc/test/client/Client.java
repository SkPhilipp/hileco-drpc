package com.hileco.drpc.test.client;

import com.hileco.drpc.test.client.api.MasterServiceImpl;
import com.hileco.drcp.todo.api.models.HTTPSubscription;
import com.hileco.drcp.todo.Router;
import com.hileco.drcp.todo.RouterClient;
import com.hileco.lib.service.EmbeddedServer;
import com.hileco.lib.service.exceptions.EmbeddedServerStartException;

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
