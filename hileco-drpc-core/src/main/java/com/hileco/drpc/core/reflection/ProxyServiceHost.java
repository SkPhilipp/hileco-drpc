package com.hileco.drpc.core.reflection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hileco.drpc.core.spec.*;
import com.hileco.drpc.core.spec.ArgumentsStreamer;
import com.hileco.drpc.core.spec.SilentCloseable;
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
 * Can be used both for hosting local services and for connecting to remote services.
 *
 * @author Philipp Gayret
 */
public class ProxyServiceHost extends ServiceHost implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyServiceHost.class);

    private final Table<String, String, MessageReceiver> consumers;
    private final MessageSender messageSender;
    private final ArgumentsStreamer argumentsStreamer;

    /**
     * @param messageSender     sender to handle callback result messages
     * @param argumentsStreamer streamer to use to create message consumers for services with
     */
    public ProxyServiceHost(MessageSender messageSender, ArgumentsStreamer argumentsStreamer) {
        this.messageSender = messageSender;
        this.argumentsStreamer = argumentsStreamer;
        this.consumers = HashBasedTable.create();
    }

    /**
     * Constructs a {@link ServiceConnector} out of the given interface class, useable for RPC and DRPC.
     *
     * @param type any RPC compliant interface
     * @param <T>  network object type
     * @return the given class' {@link ServiceConnector}
     */
    public <T> ServiceConnector<T> connector(Class<T> type) {
        return new ProxyServiceConnector<>(this, type, this.argumentsStreamer);
    }

    private SilentCloseable register(String topic, String identifier, MessageReceiver consumer) {
        synchronized (consumers) {
            if (!this.consumers.contains(topic, identifier)) {
                this.consumers.put(topic, identifier, consumer);
                return () -> {
                    synchronized (consumers) {
                        this.consumers.remove(topic, identifier);
                    }
                };
            } else {
                throw new IllegalArgumentException(topic + " " + identifier + " already taken");
            }
        }
    }

    @Override
    public <T> SilentCloseable registerService(Class<T> type, String identifier, T implementation) {
        MessageReceiver consumer = new ProxyMessageReceiver<>(type, this.argumentsStreamer, this, implementation);
        String topic = this.topic(type);
        return this.register(topic, identifier, consumer);
    }

    @Override
    public SilentCloseable registerCallback(String identifier, MessageReceiver consumer) throws IllegalArgumentException {
        return this.register(Metadata.CALLBACK_TOPIC, identifier, consumer);
    }

    @Override
    public void accept(Metadata metadata, InputStream content) throws IOException {
        // handling of service type messages
        if (metadata.getType() == Metadata.Type.SERVICE) {
            List<MessageReceiver> receivers = new ArrayList<>();
            synchronized (consumers) {
                Map<String, MessageReceiver> topicConsumers = this.consumers.row(metadata.getTopic());
                // we need to filter receivers if the message is targeted only
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
            }
            // call all receivers
            for (MessageReceiver receiver : receivers) {
                receiver.accept(metadata, content);
            }
        }
        // handling of callbacks
        if (metadata.getType() == Metadata.Type.CALLBACK) {
            MessageReceiver receiver;
            synchronized (consumers) {
                receiver = this.consumers.get(Metadata.CALLBACK_TOPIC, metadata.getReplyTo());
            }
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
