package com.hileco.drpc.core.reflection;

import com.google.common.collect.Lists;
import com.hileco.drpc.core.spec.MessageReceiver;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.spec.ServiceHost;
import com.hileco.drpc.core.spec.ArgumentsStreamer;
import com.hileco.drpc.core.reflection.util.Invocation;
import com.hileco.drpc.core.reflection.util.Invocations;
import com.hileco.drpc.core.spec.SilentCloseable;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of {@link com.hileco.drpc.core.spec.ServiceConnector}, delegating transport calls to a given {@link com.hileco.drpc.core.spec.ServiceHost}.
 *
 * @param <T> remote service type
 * @author Philipp Gayret
 */
public class ProxyServiceConnector<T> implements ServiceConnector<T> {

    public static final int TIMEOUT = 60000;

    private final ServiceHost serviceHost;
    private final Class<T> type;
    private final ClassLoader classLoader;
    private final ArgumentsStreamer argumentsStreamer;

    public ProxyServiceConnector(ServiceHost serviceHost, Class<T> type, ArgumentsStreamer argumentsStreamer) {
        this.serviceHost = serviceHost;
        this.type = type;
        this.argumentsStreamer = argumentsStreamer;
        this.classLoader = this.getClass().getClassLoader();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> SilentCloseable drpc(Function<T, R> invoker, Consumer<R> consumer) {

        Invocation invocation = Invocations.one(type, invoker::apply);

        String topic = this.serviceHost.topic(type);
        Metadata metadata = new Metadata(UUID.randomUUID().toString(), topic, invocation.getName(), null);
        this.serviceHost.send(metadata, invocation.getArguments());

        return this.serviceHost.registerService(MessageReceiver.class, metadata.getId(), (callbackMetadata, content) -> {
            R result = (R) this.argumentsStreamer.deserializeFrom(content, new Class[]{invocation.getMethod().getReturnType()})[0];
            consumer.accept(result);
        });

    }

    @SuppressWarnings("all")
    @Override
    public T connect(String identifier) {

        List<String> targets = Lists.newArrayList(identifier);
        String topic = this.serviceHost.topic(type);

        return (T) Proxy.newProxyInstance(this.classLoader, new Class[]{type}, (proxy, method, arguments) -> {

            Metadata metadata = new Metadata(UUID.randomUUID().toString(), topic, method.getName(), targets);

            Object[] results = new Object[]{null};

            if (metadata.getExpectResponse()) {
                SilentCloseable listener = this.serviceHost.registerCallback(metadata.getId(), (callbackMetadata, content) -> {
                    synchronized (results) {
                        results[0] = this.argumentsStreamer.deserializeFrom(content, new Class[]{method.getReturnType()})[0];
                        results.notifyAll();
                    }
                });
                synchronized (results) {
                    this.serviceHost.send(metadata, arguments);
                    results.wait(TIMEOUT);
                    listener.close();
                }
            } else {
                this.serviceHost.send(metadata, arguments);
            }

            return results[0];

        });

    }

}
