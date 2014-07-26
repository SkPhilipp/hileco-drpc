package com.hileco.drpc.core;

import com.hileco.drpc.core.spec.*;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.util.SilentCloseable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of all {@link ServiceHost}, instantiates {@link ProxyMessageReceiver}s to handle messages.
 *
 * @author Philipp Gayret
 */
public class ProxyServiceHost implements ServiceHost, MessageReceiver {

    private Map<String, MessageReceiver> consumerMap;
    private ArgumentsStreamer argumentsStreamer;
    private MessageSender messageSender;

    /**
     * @param argumentsStreamer streamer to use to create message consumers for services with
     * @param messageSender     client to send procedure results to as callbacks
     */
    protected ProxyServiceHost(ArgumentsStreamer argumentsStreamer, MessageSender messageSender) {
        this.argumentsStreamer = argumentsStreamer;
        this.messageSender = messageSender;
        this.consumerMap = new HashMap<>();
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
    public <T> ServiceConnector<T> connector(Class<T> type) {
        return new ProxyServiceConnector<>(this, type, this.argumentsStreamer);
    }

    @Override
    public <T> SilentCloseable bind(Class<T> iface, T implementation) {
        String topic = this.topic(iface);
        MessageReceiver consumer = new ProxyMessageReceiver(this.argumentsStreamer, this.messageSender, implementation);
        return this.bind(topic, consumer);
    }

    @Override
    public <T> SilentCloseable bind(Class<T> iface, T implementation, String identifier) {
        String topic = this.topic(iface, identifier);
        MessageReceiver consumer = new ProxyMessageReceiver(this.argumentsStreamer, this.messageSender, implementation);
        return this.bind(topic, consumer);
    }

    @Override
    public SilentCloseable bind(String topic, MessageReceiver consumer) throws IllegalArgumentException {
        if (!this.consumerMap.containsKey(topic)) {
            this.consumerMap.put(topic, consumer);
            return () -> this.consumerMap.remove(topic, consumer);
        } else {
            throw new IllegalArgumentException("Topic " + topic + " already taken");
        }
    }

    @Override
    public void accept(Metadata metadata, InputStream content) throws IOException {
        // if metadata topic is subscribed to, then send to bound consumer
        MessageReceiver consumer = this.consumerMap.get(metadata.getTopic());
        if (consumer != null) {
            consumer.accept(metadata, content);
        } else {
            throw new IllegalArgumentException("No procedure registered for topic " + metadata.getTopic());
        }
    }

    @Override
    public void publish(Metadata metadata, Object content) {
        this.messageSender.publish(metadata, content);
    }

}
