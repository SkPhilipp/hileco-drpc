package com.hileco.drpc.http;

import com.hileco.drpc.core.AbstractProxyClient;
import com.hileco.drpc.core.spec.IncomingMessageConsumer;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.util.SilentCloseable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Philipp Gayret
 */
public class DelegatingProxyClient extends AbstractProxyClient implements IncomingMessageConsumer {

    private Map<String, IncomingMessageConsumer> consumerMap;

    public DelegatingProxyClient(ArgumentsStreamer argumentsStreamer) {
        super(argumentsStreamer);
        this.consumerMap = new HashMap<>();
    }

    @Override
    public SilentCloseable bind(String topic, IncomingMessageConsumer consumer) throws IllegalArgumentException {
        if (!this.consumerMap.containsKey(topic)) {
            this.consumerMap.put(topic, consumer);
            return () -> this.consumerMap.remove(topic, consumer);
        } else {
            throw new IllegalArgumentException("Topic " + topic + " already taken");
        }
    }

    @Override
    public void publish(Metadata metadata, Object content) {

    }

    @Override
    public void accept(Metadata metadata, InputStream content) {
        // if metadata topic is subscribed to, then send to bound consumer
        IncomingMessageConsumer consumer = this.consumerMap.get(metadata.getTopic());
        if (consumer != null) {
            consumer.accept(metadata, content);
        } else {
            throw new IllegalArgumentException("No procedure registered for topic " + metadata.getTopic());
        }
    }

}
