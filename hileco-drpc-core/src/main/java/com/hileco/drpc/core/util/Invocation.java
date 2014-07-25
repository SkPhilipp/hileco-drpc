package com.hileco.drpc.core.util;

import java.lang.reflect.Method;

/**
 * Represents a method invocation information.
 *
 * @author Philipp Gayret
 */
public class Invocation {

    private final Method method;
    private final Object[] arguments;

    public Invocation(Method method, Object[] arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return method.getName();
    }

    public Object[] getArguments() {
        return arguments;
    }

}
