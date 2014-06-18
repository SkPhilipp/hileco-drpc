package com.hileco.drpc.test.service.impl;

import machine.drcp.core.api.Client;
import machine.drcp.core.api.util.Listener;
import machine.drcp.core.api.util.SilentCloseable;

public class SampleCalculatorServiceImpl extends Listener implements SampleCalculatorService {

    private final Client client;

    public SampleCalculatorServiceImpl(Client client) {
        this.client = client;
    }

    @Override
    protected SilentCloseable listen() {
        return this.client.listen(SampleCalculatorService.class, this);
    }

    @Override
    public Integer calculate(Integer a, Integer b) {
        return a + b;
    }

}
