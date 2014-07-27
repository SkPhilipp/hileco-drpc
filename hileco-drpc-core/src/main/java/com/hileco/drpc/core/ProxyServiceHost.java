package com.hileco.drpc.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hileco.drpc.core.spec.*;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.util.SilentCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link ServiceHost}, instantiates {@link ProxyMessageReceiver}s for message processing.
 * <p/>
 * Can be used both as a {@link ServiceHost}, for hosting local services, and as a {@link ServiceConnectorFactory}, for connecting to remote services.
 *
 * @author Philipp Gayret
 */
public class ProxyServiceHost extends ServiceHost implements ServiceConnectorFactory, MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyServiceHost.class);

    private Table<String, String, MessageReceiver> consumers;
    private MessageSender messageSender;
    private ArgumentsStreamer argumentsStreamer;

    /**
     * @param messageSender     sender to handle callback result messages
     * @param argumentsStreamer streamer to use to create message consumers for services with
     */
    public ProxyServiceHost(MessageSender messageSender, ArgumentsStreamer argumentsStreamer) {
        this.messageSender = messageSender;
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
        // handling of service type messages
        if (metadata.getType() == Metadata.Type.SERVICE) {
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
        // handling of callbacks
        if (metadata.getType() == Metadata.Type.CALLBACK) {
            String callbackTopic = this.topic(Callback.class);
            MessageReceiver receiver = this.consumers.get(callbackTopic, metadata.getReplyTo());
            if (receiver != null) {
                receiver.accept(metadata, content);
            } else {
                LOG.warn("No receiver for callback with replyTo {}", metadata.getReplyTo());
            }
        }
    }

    @Override
    public void send(Metadata metadata, Object[] content) {
        this.messageSender.send(metadata, content);
    }

}
