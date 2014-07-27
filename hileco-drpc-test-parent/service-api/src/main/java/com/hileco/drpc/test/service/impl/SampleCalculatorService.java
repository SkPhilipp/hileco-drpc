package com.hileco.drpc.test.service.impl;

public interface SampleCalculatorService {

    public static final String SINGLE_HOST_ID = "SampleCalculatorServiceHost";
    /**
     * Adds two numbers together.
     */
    public Integer calculate(Integer a, Integer b);

}
