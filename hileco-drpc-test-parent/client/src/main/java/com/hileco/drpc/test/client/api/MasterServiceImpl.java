package com.hileco.drpc.test.client.api;

import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.spec.ServiceHost;
import com.hileco.drpc.test.service.impl.SampleCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterServiceImpl {

    private static final int SCAN_RATE = 100;
    private static final Logger LOG = LoggerFactory.getLogger(MasterServiceImpl.class);
    private final ScheduledExecutorService scheduler;
    private ServiceConnector<SampleCalculatorService> globalConsumerServiceConnector;

    public MasterServiceImpl(ServiceHost serviceHost) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.globalConsumerServiceConnector = serviceHost.connector(SampleCalculatorService.class);
    }

    public void start() {
        this.scheduler.scheduleAtFixedRate(this::distributeTasks, 100, SCAN_RATE, TimeUnit.MILLISECONDS);
    }

    public void distributeTasks() {
        this.globalConsumerServiceConnector.drpc((d) -> d.calculate(1, 2), (r) -> LOG.info("Obtained a result: {}", r));
        this.globalConsumerServiceConnector.drpc(d -> d.calculate(1, 2), r -> LOG.info("Obtained a result: {}", r));
    }

}
