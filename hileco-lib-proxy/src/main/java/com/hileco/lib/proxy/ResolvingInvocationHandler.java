package com.hileco.lib.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * For every invocation made, it gets the object via the {@link Supplier} given at initialization.
 *
 * @param <T>
 */
public class ResolvingInvocationHandler<T> implements InvocationHandler {

    private final Supplier<T> resolver;

    public ResolvingInvocationHandler(Supplier<T> resolver) {
        this.resolver = resolver;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        T object = resolver.get();
        return method.invoke(object, args);
    }

}