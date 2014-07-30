package com.hileco.drpc.core.reflection.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Philipp Gayret
 */
public class InvocationExtractorTest {

    public interface TestInterface {

        public void example(int a, int b);

    }

    /**
     * Tests {@link Invocations#one(Class, java.util.function.Consumer)}'s ability to properly extract arguments.
     */
    @Test
    public void testExtractOneUsingFunction() throws Exception {

        Invocation invocation = Invocations.one(TestInterface.class, d -> d.example(1, 2));

        Assert.assertEquals(invocation.getArguments().length, 2);
        Assert.assertEquals(invocation.getArguments()[0], 1);
        Assert.assertEquals(invocation.getArguments()[1], 2);
        Assert.assertEquals(invocation.getName(), "example");

    }


} 
