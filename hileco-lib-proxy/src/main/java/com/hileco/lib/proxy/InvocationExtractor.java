package com.hileco.lib.proxy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Utility for simplifying extracting method invocation information.
 *
 * @author Philipp Gayret
 */
public class InvocationExtractor {

    /**
     * Returns the list of invocations made on the proxy object passed to the given `invoker` function.
     * <p/>
     * The list may change even after being returned by this method.
     *
     * @param interfaceType type to proxy
     * @param invoker       function to make calls
     * @param predicate     predicate to verify if an invocation may be added to the list
     * @return invocation list
     */
    @SuppressWarnings("unchecked")
    public <T> List<Invocation> extractUsingFunction(Class<T> interfaceType, Function<T, ?> invoker, BiPredicate<List<Invocation>, Invocation> predicate) {

        List<Invocation> invocations = new ArrayList<>();
        ClassLoader classLoader = this.getClass().getClassLoader();

        T listeningProxy = (T) Proxy.newProxyInstance(classLoader, new Class[]{interfaceType}, (proxy, method, args) -> {

            Invocation invocation = new Invocation(method, args);
            if (predicate.test(invocations, invocation)) {
                invocations.add(invocation);
            }
            return null;

        });

        invoker.apply(listeningProxy);

        return invocations;

    }

    /**
     * Returns the list of invocations made on the proxy object passed to the given `invoker` function.
     * <p/>
     * The list may change even after being returned by this method.
     *
     * @param interfaceType   type to proxy
     * @param invoker         function to make calls
     * @param invocationLimit maximum amount of invocations to allow
     * @return invocation list
     */
    public <T> List<Invocation> extractLimitedUsingFunction(Class<T> interfaceType, Function<T, ?> invoker, int invocationLimit) {
        return this.extractUsingFunction(interfaceType, invoker, (list, current) -> list.size() < invocationLimit);
    }

}
