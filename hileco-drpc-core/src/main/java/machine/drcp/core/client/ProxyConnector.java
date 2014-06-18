package machine.drcp.core.client;

import machine.drcp.core.api.Client;
import machine.drcp.core.api.Connector;
import machine.drcp.core.api.annotations.RPCTimeout;
import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.models.RPC;
import machine.drcp.core.api.util.Expirer;
import machine.drcp.core.api.util.Referenced;
import machine.drcp.core.api.util.SilentCloseable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProxyConnector<T, P> implements Connector<T, P> {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyConnector.class);

    private final Expirer expirer;
    private final ObjectConverter objectConverter;
    private final Client network;
    private final Class<T> type;

    public ProxyConnector(Client client, Class<T> type, ObjectConverter objectConverter) {
        this.expirer = new Expirer(10);
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
            SynchronousQueue<Object> synchronousQueue = new SynchronousQueue<>(true);
            network.publish(message);
            if (!method.getReturnType().equals(Void.TYPE)) {
                SilentCloseable listener = network.listen(message.getId().toString(), received -> {
                    try {
                        Object converted = objectConverter.convert(received.getContent(), method.getReturnType());
                        synchronousQueue.add(converted);
                    } catch (Exception e) {
                        LOG.error("Erred while handling a message", e);
                    }
                });
                // annotation processing
                RPCTimeout timeout = method.getAnnotation(RPCTimeout.class);
                if (timeout != null) {
                    listener = expirer.schedule(listener, timeout.value(), timeout.unit());
                    Object result = synchronousQueue.poll(timeout.value(), timeout.unit());
                    listener.close();
                    return result;
                } else {
                    listener = expirer.schedule(listener, 60, TimeUnit.SECONDS);
                    Object result = synchronousQueue.poll(60, TimeUnit.SECONDS);
                    listener.close();
                    return result;
                }
            } else {
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> SilentCloseable drpc(Function<T, R> invoker, Consumer<R> consumer) {
        Referenced<UUID> sentMessageId = new Referenced<>(null);
        Referenced<SilentCloseable> closeableListener = new Referenced<>(null);
        ClassLoader classLoader = this.getClass().getClassLoader();
        T distributingProxy = (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, args) -> {
            if (sentMessageId.value() == null) {
                // rpc message constructing, listen on callback id
                RPC rpc = new RPC(method.getName(), args);
                String topic = this.network.topic(type);
                Message<RPC> message = new Message<>(topic, rpc);
                SilentCloseable closeable = network.listen(message.getId().toString(), received -> {
                    R converted = (R) objectConverter.convert(received.getContent(), method.getReturnType());
                    consumer.accept(converted);
                });
                // annotation processing
                RPCTimeout timeout = method.getAnnotation(RPCTimeout.class);
                if (timeout != null) {
                    closeable = expirer.schedule(closeable, timeout.value(), timeout.unit());
                }
                // publish the message
                network.publish(message);
                sentMessageId.value(message.getId());
                closeableListener.value(closeable);
                return null;
            } else {
                throw new IllegalArgumentException("The given function may only call one method on the drpc proxy");
            }
        });
        invoker.apply(distributingProxy);
        if (sentMessageId.value() == null) {
            throw new IllegalArgumentException("The given function did not immediately call any method on the drpc proxy");
        } else {
            return closeableListener.value();
        }
    }

}
