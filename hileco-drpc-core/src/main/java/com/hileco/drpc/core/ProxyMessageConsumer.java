package com.hileco.drpc.core;

import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.spec.IncomingMessageConsumer;
import com.hileco.drpc.core.spec.MessageClient;
import com.hileco.drpc.core.spec.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Implementation of {@link IncomingMessageConsumer}, interprets messages as method calls.
 * <p/>
 * Any method call results will be sent to {@link #client} as callback messages.
 *
 * @author Philipp Gayret
 */
public class ProxyMessageConsumer implements IncomingMessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyMessageConsumer.class);

    private ArgumentsStreamer argumentsStreamer;
    private MessageClient client;
    private Object receiver;

    public ProxyMessageConsumer(ArgumentsStreamer argumentsStreamer, MessageClient client, Object receiver) {
        this.argumentsStreamer = argumentsStreamer;
        this.client = client;
        this.receiver = receiver;
    }

    /**
     * Accepts any message and performs a method invocation on the local {@link #receiver}, sends callback results through {@link #client}.
     * <p/>
     * Consumes all errors and forwards them to the {@link #LOG}.
     *
     * @param metadata      metadata describing how to handle the content
     * @param contentStream actual content to process
     */
    @Override
    public void accept(Metadata metadata, InputStream contentStream) {
        try {
            Method[] methods = receiver.getClass().getMethods();
            Method match = null;
            for (Method method : methods) {
                if (method.getName().equals(metadata.getOperation())) {
                    match = method;
                    break;
                }
            }
            if (match != null) {
                Class<?>[] parameterTypes = match.getParameterTypes();
                Object[] convertedArgs = argumentsStreamer.deserializeFrom(contentStream, parameterTypes);
                try {
                    if (!match.getReturnType().equals(Void.TYPE)) {
                        Object result = match.invoke(this.receiver, convertedArgs);
                        Metadata callbackMeta = new Metadata(null, metadata.getId());
                        client.publish(callbackMeta, result);
                    } else {
                        match.invoke(this.receiver, convertedArgs);
                    }
                } catch (ReflectiveOperationException e) {
                    LOG.warn("Unable to process a call, erred while invoking. receiver type {}, metadata {}", receiver.getClass(), metadata, e);
                }
            } else {
                LOG.error("Unable to process a call, no matching method. receiver type {}, metadata {}", receiver.getClass(), metadata);
            }
        } catch (Exception e) {
            LOG.error("Erred while reading streamed content", e);
        }
    }

}
