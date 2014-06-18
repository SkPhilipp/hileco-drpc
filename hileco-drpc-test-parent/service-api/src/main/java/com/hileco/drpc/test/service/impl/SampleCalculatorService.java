package com.hileco.drpc.test.service.impl;

import machine.drcp.core.api.annotations.RPCTimeout;

public interface SampleCalculatorService {

    /**
     * Adds two numbers together.
     */
    @RPCTimeout(5)
    public Integer calculate(Integer a, Integer b);

}
