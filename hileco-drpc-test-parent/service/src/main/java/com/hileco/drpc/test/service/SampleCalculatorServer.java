package com.hileco.drpc.test.service;

import com.hileco.drpc.test.service.impl.SampleCalculatorServiceImpl;
import machine.drcp.http.api.models.HTTPSubscription;
import machine.drcp.http.impl.RouterClient;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;

import java.util.HashSet;
import java.util.Set;

public class SampleCalculatorServer {

    public void start(String routerURL, Integer port) throws EmbeddedServerStartException {

        RouterClient routerClient = new RouterClient(routerURL, () -> {
            HTTPSubscription subscription = new HTTPSubscription();
            subscription.setPort(port);
            return subscription;
        });

        EmbeddedServer embeddedServer = new EmbeddedServer(port);
        Set<Object> services = new HashSet<>();
        services.add(routerClient);
        embeddedServer.start(services);
        SampleCalculatorServiceImpl consumerImpl = new SampleCalculatorServiceImpl( routerClient.getClient());
        consumerImpl.start();

    }

}
