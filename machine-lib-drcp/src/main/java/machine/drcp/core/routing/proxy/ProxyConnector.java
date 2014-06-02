package machine.drcp.core.routing.proxy;

import machine.drcp.core.api.Connector;
import machine.drcp.core.api.MessageClient;
import machine.drcp.core.api.entities.Message;
import machine.drcp.core.api.entities.RPCMessage;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProxyConnector<T, P> implements Connector<T, P> {

    private final ObjectConverter objectConverter;
    private final MessageClient network;
    private final Class<T> type;

    public ProxyConnector(MessageClient network, Class<T> type, ObjectConverter objectConverter) {
        this.type = type;
        this.network = network;
        this.objectConverter = objectConverter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T connect(Object identifier) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, args) -> {
            RPCMessage rpcMessage = new RPCMessage(method.getName(), args);
            String topic = this.network.topic(type, identifier);
            Message<RPCMessage> message = new Message<>(topic, rpcMessage);
            network.publish(message);
            // TODO: return `converted` via a future`
            if (!method.getReturnType().equals(Void.TYPE)) {
                network.listen(message.getMessageId().toString(), received -> {
                    Object converted = objectConverter.convert(received.getContent(), method.getReturnType());
                });
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> AutoCloseable drpc(Function<T, R> invoker, Consumer<R> consumer) {
        AtomicReference<UUID> sentMessageId = new AtomicReference<>();
        AtomicReference<AutoCloseable> closeableListener = new AtomicReference<>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        T distributingProxy = (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, args) -> {
            if (sentMessageId.get() != null) {
                RPCMessage rpcMessage = new RPCMessage(method.getName(), args);
                String topic = this.network.topic(type);
                Message<RPCMessage> message = new Message<>(topic, rpcMessage);
                network.publish(message);
                AutoCloseable closeable = network.listen(message.getMessageId().toString(), received -> {
                    R converted = (R) objectConverter.convert(received.getContent(), method.getReturnType());
                    consumer.accept(converted);
                });
                network.publish(message);
                sentMessageId.set(message.getMessageId());
                closeableListener.set(closeable);
                return null;
            } else {
                throw new IllegalArgumentException("The given function may only call one method on the drpc proxy");
            }
        });
        invoker.apply(distributingProxy);
        if (sentMessageId.get() == null) {
            throw new IllegalArgumentException("The given function did not immediately call any method on the drpc proxy");
        } else {
            return closeableListener.get();
        }
    }

}
