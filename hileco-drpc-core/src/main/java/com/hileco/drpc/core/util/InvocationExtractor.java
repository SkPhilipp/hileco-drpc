package com.hileco.drpc.core.util;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

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
     * @param invoker       consumer which should make calls on the proxy it'll be given
     * @param predicate     predicate to verify if an invocation may be added to the list
     * @return invocation list
     */
    @SuppressWarnings("unchecked")
    public <T> List<Invocation> extractUsingFunction(Class<T> interfaceType, Consumer<T> invoker, BiPredicate<List<Invocation>, Invocation> predicate) {

        List<Invocation> invocations = new ArrayList<>();
        ClassLoader classLoader = this.getClass().getClassLoader();

        T listeningProxy = (T) Proxy.newProxyInstance(classLoader, new Class[]{interfaceType}, (proxy, method, args) -> {

            Invocation invocation = new Invocation(method, args);
            if (predicate.test(invocations, invocation)) {
                invocations.add(invocation);
            }
            return null;

        });

        invoker.accept(listeningProxy);

        return invocations;

    }

    /**
     * Returns the single invocation the given invoker must make when it is given a proxy object.
     *
     * @param interfaceType type to proxy
     * @param invoker       consumer which should make calls on the proxy it'll be given
     * @return a single invocation
     */
    public <T> Invocation extractOneUsingFunction(Class<T> interfaceType, Consumer<T> invoker) {
        List<Invocation> invocations = this.extractUsingFunction(interfaceType, invoker, (list, current) -> list.size() < 1);
        if (invocations.size() < 1) {
            throw new IllegalArgumentException("Given invoker must make at least one call on " + interfaceType.getName());
        }
        return invocations.get(0);
    }

}
