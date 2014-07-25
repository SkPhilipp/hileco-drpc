package com.hileco.drpc.test.service.impl;

import com.hileco.drpc.core.spec.Client;
import com.hileco.drpc.core.util.SilentCloseable;

public class SampleCalculatorServiceImpl implements SampleCalculatorService {

    private final Client client;

    public SampleCalculatorServiceImpl(Client client) {
        this.client = client;
    }

    public SilentCloseable listen() {
        return this.client.listen(SampleCalculatorService.class, this);
    }

    @Override
    public Integer calculate(Integer a, Integer b) {
        return a + b;
    }

}
