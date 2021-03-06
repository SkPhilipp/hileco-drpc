package com.hileco.drpc.core.reflection;

import com.hileco.drpc.core.spec.MessageReceiver;
import com.hileco.drpc.core.spec.MessageSender;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.spec.ArgumentsStreamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Implementation of {@link com.hileco.drpc.core.spec.MessageReceiver}, interprets messages as method calls.
 * <p/>
 * Any method call results will be sent to {@link #client} as callback messages.
 *
 * @author Philipp Gayret
 */
public class ProxyMessageReceiver<T> implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyMessageReceiver.class);

    private Class<T> type;
    private ArgumentsStreamer argumentsStreamer;
    private MessageSender client;
    private Object receiver;

    public ProxyMessageReceiver(Class<T> type, ArgumentsStreamer argumentsStreamer, MessageSender client, T receiver) {
        this.type = type;
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
            Method[] methods = this.type.getMethods();
            Method match = null;
            for (Method method : methods) {
                if (method.getName().equals(metadata.getOperation())) {
                    match = method;
                    break;
                }
            }
            if (match != null) {
                try {
                    Class<?>[] parameterTypes = match.getParameterTypes();
                    Object[] convertedArgs = this.argumentsStreamer.deserializeFrom(contentStream, parameterTypes);
                    Object result = match.invoke(this.receiver, convertedArgs);
                    Metadata callbackMeta = new Metadata(UUID.randomUUID().toString(), metadata.getId());
                    client.send(callbackMeta, new Object[]{result});
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
