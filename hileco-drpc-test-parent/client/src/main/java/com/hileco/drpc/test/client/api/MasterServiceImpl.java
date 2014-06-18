package com.hileco.drpc.test.client.api;

import com.hileco.drpc.test.service.impl.SampleCalculatorService;
import machine.drcp.core.api.Client;
import machine.drcp.core.api.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterServiceImpl {

    private static final int SCAN_RATE = 100;
    private static final Logger LOG = LoggerFactory.getLogger(MasterServiceImpl.class);
    private final ScheduledExecutorService scheduler;
    private Connector<SampleCalculatorService, ?> globalConsumerConnector;

    public MasterServiceImpl(Client client) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.globalConsumerConnector = client.connector(SampleCalculatorService.class);
    }

    public void start() {
        this.scheduler.scheduleAtFixedRate(this::distributeTasks, 100, SCAN_RATE, TimeUnit.MILLISECONDS);
    }

    public void distributeTasks() {
        this.globalConsumerConnector.drpc((d) -> d.calculate(1, 2), (r) -> LOG.info("Obtained a result: {}", r));
    }

}
