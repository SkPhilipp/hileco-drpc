package com.hileco.drpc.core;

import com.hileco.drpc.core.spec.ServiceConnector;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests for {@link LocalServiceHost} and indirectly {@link LocalServicesConnector}.
 *
 * @author Philipp Gayret
 */
public class LocalServiceTest {

    public static interface SampleService {

        public Integer calculate(Integer a, Integer b);

    }

    /**
     * Registers a bunch of sample services that do calculations on two numbers using a {@link LocalServiceHost},
     * then performs a drpc and stores all results into a set, and finally verifies the set contains all results.
     */
    @Test
    public void test() {

        LocalServiceHost localServiceHost = new LocalServiceHost();
        localServiceHost.registerService(SampleService.class, "add", (a, b) -> a + b);
        localServiceHost.registerService(SampleService.class, "multiply", (a, b) -> a * b);
        localServiceHost.registerService(SampleService.class, "divide", (a, b) -> a / b);

        ServiceConnector<SampleService> connector = localServiceHost.connector(SampleService.class);

        Set<Integer> results = new HashSet<>();

        connector.drpc((sampleService) -> sampleService.calculate(3, 3), results::add);

        Assert.assertTrue(results.contains(6)); // 3 + 3
        Assert.assertTrue(results.contains(9)); // 3 * 3
        Assert.assertTrue(results.contains(1)); // 3 / 3

    }

}
