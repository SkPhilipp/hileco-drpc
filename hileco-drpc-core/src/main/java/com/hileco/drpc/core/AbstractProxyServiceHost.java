package com.hileco.drpc.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hileco.drpc.core.spec.MessageReceiver;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.spec.ServiceHost;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.util.SilentCloseable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Partial implementation of {@link ServiceHost}, instantiates {@link ProxyMessageReceiver}s to handle messages.
 *
 * @author Philipp Gayret
 */
public abstract class AbstractProxyServiceHost extends ServiceHost implements MessageReceiver {

    private Table<String, String, MessageReceiver> consumers;
    private ArgumentsStreamer argumentsStreamer;

    /**
     * @param argumentsStreamer streamer to use to create message consumers for services with
     */
    public AbstractProxyServiceHost(ArgumentsStreamer argumentsStreamer) {
        this.argumentsStreamer = argumentsStreamer;
        this.consumers = HashBasedTable.create();
    }

    @Override
    public <T> ServiceConnector<T> connector(Class<T> type) {
        return new ProxyServiceConnector<>(this, type, this.argumentsStreamer);
    }

    @Override
    public <T> SilentCloseable registerService(Class<T> type, String identifier, T implementation) {
        MessageReceiver consumer = new ProxyMessageReceiver(this.argumentsStreamer, this, implementation);
        return this.registerReceiver(type, identifier, consumer);
    }

    @Override
    public SilentCloseable registerReceiver(Class<?> type, String identifier, MessageReceiver consumer) throws IllegalArgumentException {
        String topic = this.topic(type);
        if (!this.consumers.contains(topic, identifier)) {
            this.consumers.put(topic, identifier, consumer);
            return () -> this.consumers.remove(topic, identifier);
        } else {
            throw new IllegalArgumentException("Identifier " + identifier + " on type " + type + " with topic " + topic + " already taken");
        }
    }

    @Override
    public void accept(Metadata metadata, InputStream content) throws IOException {
        Map<String, MessageReceiver> topicConsumers = this.consumers.row(metadata.getTopic());
        // we need to filter receivers if the message is targeted only
        List<MessageReceiver> receivers = new ArrayList<>();
        if (metadata.hasTargets()) {
            for (String target : metadata.getTargets()) {
                MessageReceiver receiver = topicConsumers.get(target);
                if (receiver != null) {
                    receivers.add(receiver);
                }
            }
        } else {
            receivers.addAll(topicConsumers.values());
        }
        // call all receivers
        for (MessageReceiver receiver : receivers) {
            receiver.accept(metadata, content);
        }
    }

}
