package com.hileco.drpc.core;

import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.util.SilentCloseable;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An implementation of {@link ServiceConnector}, querying a map of given services.
 *
 * @param <T> service type
 * @author Philipp Gayret
 */
public class LocalServicesConnector<T> implements ServiceConnector<T> {

    private Map<String, T> services;

    /**
     * @param services a map of services which is allowed to change, used for read only
     */
    public LocalServicesConnector(Map<String, T> services) {
        this.services = services;
    }

    @Override
    public T connect(String identifier) {
        return services.get(identifier);
    }

    @Override
    public <R> SilentCloseable drpc(Function<T, R> invoker, Consumer<R> consumer) {
        for (T service : services.values()) {
            R result = invoker.apply(service);
            consumer.accept(result);
        }
        return SilentCloseable.NULL;
    }
}
