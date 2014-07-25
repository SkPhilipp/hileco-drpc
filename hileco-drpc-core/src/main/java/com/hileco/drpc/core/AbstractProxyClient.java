package com.hileco.drpc.core;

import com.hileco.drpc.core.spec.*;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.util.SilentCloseable;

/**
 * Implementation of all higher level methods defined in {@link com.hileco.drpc.core.spec.Client}.
 * <p/>
 * Only needs implementation for the {@link Client#bind(String, IncomingMessageConsumer)} and {@link MessageClient#publish(Metadata, Object)}.
 *
 * @author Philipp Gayret
 */
public abstract class AbstractProxyClient implements Client {

    private ArgumentsStreamer argumentsStreamer;

    protected AbstractProxyClient(ArgumentsStreamer argumentsStreamer) {
        this.argumentsStreamer = argumentsStreamer;
    }

    @Override
    public String topic(Class<?> iface) {
        return iface.getName();
    }

    @Override
    public String topic(Class<?> iface, Object identifier) {
        return String.format("%s:%s", iface.getName(), identifier);
    }

    @Override
    public <T> Connector<T> connector(Class<T> type) {
        return new ProxyConnector<>(this, type, argumentsStreamer);
    }

    @Override
    public <T> SilentCloseable listen(Class<T> iface, T implementation) {
        String topic = this.topic(iface);
        IncomingMessageConsumer consumer = new ProxyMessageConsumer(argumentsStreamer, this, implementation);
        return this.bind(topic, consumer);
    }

    @Override
    public <T> SilentCloseable listen(Class<T> iface, T implementation, String identifier) {
        String topic = this.topic(iface, identifier);
        IncomingMessageConsumer consumer = new ProxyMessageConsumer(argumentsStreamer, this, implementation);
        return this.bind(topic, consumer);
    }

}
