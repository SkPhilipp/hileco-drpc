package machine.drcp.core.client;

import machine.drcp.core.api.Client;
import machine.drcp.core.api.Connector;
import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.models.RPC;
import machine.drcp.core.api.util.SilentCloseable;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProxyConnector<T, P> implements Connector<T, P> {

    private final ObjectConverter objectConverter;
    private final Client network;
    private final Class<T> type;

    public ProxyConnector(Client client, Class<T> type, ObjectConverter objectConverter) {
        this.type = type;
        this.network = client;
        this.objectConverter = objectConverter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T connect(Object identifier) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, args) -> {
            RPC rpc = new RPC(method.getName(), args);
            String topic = this.network.topic(type, identifier);
            Message<RPC> message = new Message<>(topic, rpc);
            network.publish(message);
            SynchronousQueue<Object> synchronousQueue = new SynchronousQueue<>(true);
            if (!method.getReturnType().equals(Void.TYPE)) {
                SilentCloseable listener = network.listen(message.getId().toString(), received -> {
                    Object converted = objectConverter.convert(received.getContent(), method.getReturnType());
                    synchronousQueue.add(converted);
                });
                Object result = synchronousQueue.poll(60, TimeUnit.SECONDS);
                listener.close();
                return result;
            }
            else{
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> SilentCloseable drpc(Function<T, R> invoker, Consumer<R> consumer) {
        AtomicReference<UUID> sentMessageId = new AtomicReference<>(null);
        AtomicReference<SilentCloseable> closeableListener = new AtomicReference<>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        T distributingProxy = (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, args) -> {
            if (sentMessageId.get() == null) {
                RPC rpc = new RPC(method.getName(), args);
                String topic = this.network.topic(type);
                Message<RPC> message = new Message<>(topic, rpc);
                network.publish(message);
                SilentCloseable closeable = network.listen(message.getId().toString(), received -> {
                    R converted = (R) objectConverter.convert(received.getContent(), method.getReturnType());
                    consumer.accept(converted);
                });
                network.publish(message);
                sentMessageId.set(message.getId());
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
