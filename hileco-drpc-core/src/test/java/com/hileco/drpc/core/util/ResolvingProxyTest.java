package com.hileco.drpc.core.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Philipp Gayret
 */
public class ResolvingProxyTest {

    public interface MagicCalculator {

        public int doMagic(int a, int b);

    }

    /**
     * Tests resolving proxies, checks if calls made on the proxy are calls made on the supplier's results.
     */
    @Test
    public void testExtractLimitedUsingFunction() throws Exception {

        ThreadLocal<MagicCalculator> threadLocal = new ThreadLocal<>();
        ResolvingProxyFactory resolvingProxyFactory = new ResolvingProxyFactory();
        MagicCalculator proxyCalculator = resolvingProxyFactory.create(MagicCalculator.class, threadLocal::get);

        threadLocal.set((a, b) -> a * b);
        Assert.assertEquals(proxyCalculator.doMagic(10, 20), threadLocal.get().doMagic(10, 20));
        threadLocal.set((a, b) -> a + b);
        Assert.assertEquals(proxyCalculator.doMagic(10, 20), threadLocal.get().doMagic(10, 20));
        threadLocal.set((a, b) -> a / b);
        Assert.assertEquals(proxyCalculator.doMagic(10, 20), threadLocal.get().doMagic(10, 20));
        threadLocal.set((a, b) -> a - b);
        Assert.assertEquals(proxyCalculator.doMagic(10, 20), threadLocal.get().doMagic(10, 20));

    }

}
