package machine.drcp.core.client;

import machine.drcp.core.api.Client;
import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.models.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * A consumer of {@link machine.drcp.core.api.models.Message}, assuming of type {@link machine.drcp.core.api.models.RPC}.
 */
public class ProxyMessageConsumer implements Consumer<Message<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyMessageConsumer.class);
    private Client client;
    private Object receiver;
    private ObjectConverter objectConverter;

    public ProxyMessageConsumer(Client client, Object receiver, ObjectConverter objectConverter) {
        this.client = client;
        this.receiver = receiver;
        this.objectConverter = objectConverter;
    }

    /**
     * Accepts any message, extracts the internal {@link machine.drcp.core.api.models.RPC} descriptor and performs
     * the method invocation on the local {@link #receiver}.
     *
     * @param message the message to process as a method invocation
     */
    @Override
    public void accept(Message<?> message) {
        RPC invocationMessage = objectConverter.convert(message.getContent(), RPC.class);
        Method[] methods = receiver.getClass().getMethods();
        Method match = null;
        for (Method method : methods) {
            if (method.getName().equals(invocationMessage.getMethod())) {
                match = method;
                break;
            }
        }
        if (match != null) {
            Class<?>[] parameterTypes = match.getParameterTypes();
            Object[] convertedArgs = new Object[parameterTypes.length];
            Object[] originalArgs = invocationMessage.getArguments();
            for (int i = 0; i < parameterTypes.length; i++) {
                Object converted = objectConverter.convert(originalArgs[i], parameterTypes[i]);
                convertedArgs[i] = converted;
            }
            try {
                if (!match.getReturnType().equals(Void.TYPE)) {
                    Object result = match.invoke(this.receiver, convertedArgs);
                    Message<?> reportMessage = new Message<>(message.getMessageId().toString(), result);
                    client.publish(reportMessage);
                } else {
                    match.invoke(this.receiver, convertedArgs);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.error("Unable to process a call ( erred ) \"{}:{}({})\"", receiver.getClass(), invocationMessage.getMethod(), invocationMessage.getArguments(), e);
            }
        } else {
            LOG.error("Unable to process a call ( no match ) \"{}:{}({})\"", receiver.getClass(), invocationMessage.getMethod(), invocationMessage.getArguments(), new Exception());
        }
    }

}
