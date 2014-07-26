package com.hileco.drpc.test.service.impl;

import com.hileco.drpc.core.spec.ServiceHost;
import com.hileco.drpc.core.util.SilentCloseable;

public class SampleCalculatorServiceImpl implements SampleCalculatorService {

    private final ServiceHost serviceHost;

    public SampleCalculatorServiceImpl(ServiceHost serviceHost) {
        this.serviceHost = serviceHost;
    }

    public SilentCloseable listen() {
        return this.serviceHost.bind(SampleCalculatorService.class, this);
    }

    @Override
    public Integer calculate(Integer a, Integer b) {
        return a + b;
    }

}
