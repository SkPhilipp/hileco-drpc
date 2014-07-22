package com.hileco.drcp.core.client;

import com.hileco.drcp.core.api.Client;
import com.hileco.drcp.core.api.Connector;
import com.hileco.drcp.core.api.annotations.RPCTimeout;
import com.hileco.drcp.core.api.models.Message;
import com.hileco.drcp.core.api.models.RPC;
import com.hileco.drcp.core.api.util.Expirer;
import com.hileco.drcp.core.api.util.SilentCloseable;
import com.hileco.lib.proxy.Invocation;
import com.hileco.lib.proxy.InvocationExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ProxyConnector<T, P> implements Connector<T, P> {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyConnector.class);

    private final InvocationExtractor invocationExtractor;
    private final Expirer expirer;
    private final ObjectConverter objectConverter;
    private final Client network;
    private final Class<T> type;

    public ProxyConnector(Client client, Class<T> type, ObjectConverter objectConverter) {
        this.expirer = new Expirer(10);
        this.type = type;
        this.network = client;
        this.objectConverter = objectConverter;
        this.invocationExtractor = new InvocationExtractor();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T connect(Object identifier) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, args) -> {
            RPC rpc = new RPC(method.getName(), args);
            String topic = this.network.topic(type, identifier);
            Message<RPC> message = new Message<>(topic, rpc);
            SynchronousQueue<Object> synchronousQueue = new SynchronousQueue<>(true);
            network.publish(message);
            if (!method.getReturnType().equals(Void.TYPE)) {
                SilentCloseable listener = network.listen(message.getId().toString(), received -> {
                    try {
                        Object converted = objectConverter.convert(received.getContent(), method.getReturnType());
                        synchronousQueue.add(converted);
                    } catch (Exception e) {
                        LOG.error("Erred while handling a message", e);
                    }
                });
                // annotation processing
                RPCTimeout timeout = method.getAnnotation(RPCTimeout.class);
                if (timeout != null) {
                    Object result = synchronousQueue.poll(timeout.value(), timeout.unit());
                    listener.close();
                    return result;
                } else {
                    Object result = synchronousQueue.poll(60, TimeUnit.SECONDS);
                    listener.close();
                    return result;
                }
            } else {
                return null;
            }
        });
    }

    public Invocation buildInvocation(Consumer<T> invoker) {
        List<Invocation> invocations = invocationExtractor.extractLimitedUsingFunction(type, invoker, 1);
        if (invocations.size() == 1) {
            return invocations.get(0);
        } else {
            throw new IllegalArgumentException("The given function did not immediately call any method on the drpc proxy");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> SilentCloseable drpc(Consumer<T> invoker, Consumer<R> consumer) {
        // construct rpc message, and listen on callback id
        Invocation invocation = this.buildInvocation(invoker);
        RPC rpc = new RPC(invocation.getName(), invocation.getArguments());
        String topic = this.network.topic(type);
        Message<RPC> message = new Message<>(topic, rpc);
        SilentCloseable closeable = network.listen(message.getId().toString(), received -> {
            R converted = (R) objectConverter.convert(received.getContent(), invocation.getMethod().getReturnType());
            consumer.accept(converted);
        });
        // annotation processing
        // TODO: RPC annotations should have their own meta-annotations and be scanned for, instead of being hardcoded here
        // TODO: RPC annotations should have some sort of aop-esque system to hook into, instead of being hardcoded here
        RPCTimeout timeout = invocation.getMethod().getAnnotation(RPCTimeout.class);
        if (timeout != null) {
            closeable = expirer.schedule(closeable, timeout.value(), timeout.unit());
        }
        // publish the message
        network.publish(message);
        return closeable;

    }

}
