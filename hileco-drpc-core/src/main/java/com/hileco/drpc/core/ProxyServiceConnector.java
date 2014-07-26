package com.hileco.drpc.core;

import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.spec.ServiceHost;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.util.Invocation;
import com.hileco.drpc.core.util.InvocationExtractor;
import com.hileco.drpc.core.util.SilentCloseable;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of {@link com.hileco.drpc.core.spec.ServiceConnector}, delegating transport calls to a given {@link com.hileco.drpc.core.spec.ServiceHost}.
 *
 * @param <T> remote service type
 * @author Philipp Gayret
 */
public class ProxyServiceConnector<T> implements ServiceConnector<T> {

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

        InvocationExtractor invocationExtractor = new InvocationExtractor();
        Invocation invocation = invocationExtractor.extractOneUsingFunction(type, invoker::apply);

        String topic = this.serviceHost.topic(type);
        Metadata metadata = new Metadata(null, topic, invocation.getName());
        metadata.setExpectResponse(!invocation.getMethod().getReturnType().equals(Void.TYPE));
        this.serviceHost.publish(metadata, invocation.getArguments());

        return this.serviceHost.bind(metadata.getId(), (callbackMetadata, content) -> {
            R result = (R) this.argumentsStreamer.deserializeFrom(content, new Class[]{invocation.getMethod().getReturnType()})[0];
            consumer.accept(result);
        });

    }

    @SuppressWarnings("unchecked")
    @Override
    public T connect(String identifier) {

        String topic = this.serviceHost.topic(type, identifier);

        return (T) Proxy.newProxyInstance(this.classLoader, new Class[]{type}, (proxy, method, arguments) -> {

            Metadata metadata = new Metadata(null, topic, method.getName());
            metadata.setExpectResponse(!method.getReturnType().equals(Void.TYPE));
            this.serviceHost.publish(metadata, arguments);

            Object[] results = new Object[]{null};

            synchronized (results) {

                if (metadata.getExpectResponse()) {
                    SilentCloseable listener = this.serviceHost.bind(metadata.getId(), (callbackMetadata, content) -> {
                        results[0] = this.argumentsStreamer.deserializeFrom(content, new Class[]{method.getReturnType()})[0];
                        results.notify();
                    });

                    results.wait(60000);
                    listener.close();
                }

            }

            return results[0];

        });

    }

}
