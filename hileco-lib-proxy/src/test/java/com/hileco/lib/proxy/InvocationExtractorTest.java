package com.hileco.lib.proxy;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Philipp Gayret
 */
public class InvocationExtractorTest {

    public interface TestInterface {

        public void example(int a, int b);

    }

    /**
     * Tests `extractUsingFunction`, checking if the results returned are actually the invocation parameters.
     */
    @Test
    public void testExtractUsingFunction() throws Exception {

        // make an invocation extractor and perform do a couple of calls on the interface
        InvocationExtractor invocationExtractor = new InvocationExtractor();
        List<Invocation> invocations = invocationExtractor.extractUsingFunction(TestInterface.class, (d) -> {
            d.example(1, 2);
            d.example(3, 4);
            d.example(5, 6);
        }, (a, b) -> true);

        Assert.assertEquals(invocations.size(), 3);
        Assert.assertEquals(invocations.get(0).getArguments().length, 2);
        Assert.assertEquals(invocations.get(0).getArguments()[0], 1);
        Assert.assertEquals(invocations.get(0).getArguments()[1], 2);
        Assert.assertEquals(invocations.get(0).getMethod().getName(), "example");

    }

    /**
     * Tests `extractLimitedUsingFunction`, checking if the `invocationLimit` actually limits the size of the invocations result.
     */
    @Test
    public void testExtractLimitedUsingFunction() throws Exception {

        final int invocationLimit = 1;

        // make an invocation extractor and perform do a couple of calls on the interface
        InvocationExtractor invocationExtractor = new InvocationExtractor();

        List<Invocation> invocations = invocationExtractor.extractLimitedUsingFunction(TestInterface.class, (d) -> {
            d.example(1, 2);
            d.example(3, 4);
            d.example(5, 6);
        }, invocationLimit);

        Assert.assertEquals(invocations.size(), invocationLimit);

    }


} 
