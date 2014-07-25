package com.hileco.drpc.core;

import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.util.Invocation;
import com.hileco.drpc.core.util.InvocationExtractor;
import com.hileco.drpc.core.spec.Client;
import com.hileco.drpc.core.spec.Connector;
import com.hileco.drpc.core.util.SilentCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of {@link com.hileco.drpc.core.spec.Connector}, delegating transport calls to a given {@link com.hileco.drpc.core.spec.Client}.
 *
 * @param <T> remote service type
 * @author Philipp Gayret
 */
public class ProxyConnector<T> implements Connector<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyConnector.class);

    private final Client client;
    private final Class<T> type;
    private final ClassLoader classLoader;
    private final ArgumentsStreamer argumentsStreamer;

    public ProxyConnector(Client client, Class<T> type, ArgumentsStreamer argumentsStreamer) {
        this.client = client;
        this.type = type;
        this.argumentsStreamer = argumentsStreamer;
        this.classLoader = this.getClass().getClassLoader();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> SilentCloseable drpc(Function<T, R> invoker, Consumer<R> consumer) {

        InvocationExtractor invocationExtractor = new InvocationExtractor();
        Invocation invocation = invocationExtractor.extractOneUsingFunction(type, invoker::apply);

        String topic = client.topic(type);
        Metadata metadata = new Metadata(null, topic, invocation.getName());
        client.publish(metadata, invocation.getArguments());

        return client.bind(metadata.getId(), (callbackMetadata, content) -> {
            R result = (R) argumentsStreamer.deserializeFrom(content, new Class[]{invocation.getMethod().getReturnType()})[0];
            consumer.accept(result);
        });

    }

    @SuppressWarnings("unchecked")
    @Override
    public T connect(String identifier) {

        String topic = client.topic(type, identifier);

        return (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, arguments) -> {

            Metadata metadata = new Metadata(null, topic, method.getName());
            client.publish(metadata, arguments);

            Object[] results = new Object[]{null};
            if (!method.getReturnType().equals(Void.TYPE)) {
                SilentCloseable listener = client.bind(metadata.getId(), (callbackMetadata, content) -> {
                    results[0] = argumentsStreamer.deserializeFrom(content, new Class[]{method.getReturnType()})[0];
                    results.notify();
                });

                results.wait(60000);
                listener.close();
            }
            return results[0];

        });

    }

}
