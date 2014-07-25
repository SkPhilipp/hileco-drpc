package com.hileco.drpc.core.util;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

/**
 * Instantiates proxies using a {@link ResolvingInvocationHandler}.
 *
 * @author Philipp Gayret
 */
public class ResolvingProxyFactory {

    private ClassLoader classLoader = this.getClass().getClassLoader();

    /**
     * Creates a new resolving proxy, using the given {@link Supplier}.
     *
     * @param resolver object resolver
     * @param iface    interface the proxy should implement
     * @param <T>      type to return
     * @return a proxy
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> iface, Supplier<T> resolver) {
        ResolvingInvocationHandler handler = new ResolvingInvocationHandler(resolver);
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{iface}, handler);
    }

}
