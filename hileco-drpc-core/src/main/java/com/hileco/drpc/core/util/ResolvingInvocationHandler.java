package com.hileco.drpc.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * For every invocation made, it gets the object via the {@link Supplier} given at initialization.
 *
 * @author Philipp Gayret
 */
public class ResolvingInvocationHandler implements InvocationHandler {

    private final Supplier<?> resolver;

    public ResolvingInvocationHandler(Supplier<?> resolver) {
        this.resolver = resolver;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object object = resolver.get();
        return method.invoke(object, args);
    }

}