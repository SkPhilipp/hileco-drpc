package machine.lib.message.proxy;

import machine.lib.message.api.Invokeable;
import machine.lib.message.api.Network;
import machine.lib.message.api.Networked;
import machine.lib.message.api.util.ObjectConverter;
import machine.router.api.entities.NetworkInvocationMessage;
import machine.router.api.entities.NetworkMessage;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProxyNetworked<T extends Invokeable, P> implements Networked<T> {

    private final ObjectConverter objectConverter;
    private final Network network;
    private final Class<T> type;
    private final T resultlessProxy;
    private final P binding;

    @SuppressWarnings("unchecked")
    public ProxyNetworked(Network network, Class<T> type, P binding, ObjectConverter objectConverter) {
        this.type = type;
        this.network = network;
        this.binding = binding;
        this.objectConverter = objectConverter;
        ClassLoader classLoader = this.getClass().getClassLoader();
        this.resultlessProxy = (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, args) -> {
            NetworkInvocationMessage invocationMessage = new NetworkInvocationMessage(method.getName(), args, false);
            NetworkMessage networkMessage = new NetworkMessage(getTopic(), invocationMessage);
            network.publishMessage(networkMessage);
            return null;
        });
    }

    /**
     * @return the topic of the invokeable
     */
    private String getTopic() {
        if (binding != null) {
            return ProxyNetworkTopics.getTopic(type, binding);
        } else {
            return ProxyNetworkTopics.getTopic(type);
        }
    }

    @Override
    public T getImplementation() {
        return resultlessProxy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> AutoCloseable callDistributed(Function<T, R> invoker, Consumer<R> consumer) {
        AtomicReference<UUID> sentMessageId = new AtomicReference<>(null);
        ClassLoader classLoader = this.getClass().getClassLoader();
        T distributingProxy = (T) Proxy.newProxyInstance(classLoader, new Class[]{type}, (proxy, method, args) -> {
            if (sentMessageId.get() != null) {
                NetworkInvocationMessage invocationMessage = new NetworkInvocationMessage(method.getName(), args, true);
                NetworkMessage networkMessage = new NetworkMessage(getTopic(), invocationMessage);
                sentMessageId.set(networkMessage.getMessageId());
                network.beginListen(networkMessage.getMessageId().toString(), message -> {
                    R converted = (R) objectConverter.convert(message.getContent(), method.getReturnType());
                    consumer.accept(converted);
                });
                network.publishMessage(networkMessage);
                return null;
            } else {
                throw new IllegalArgumentException("The given function called too many methods on the proxy");
            }
        });
        invoker.apply(distributingProxy);
        if (sentMessageId.get() == null) {
            throw new IllegalArgumentException("The given function did not call any method on the proxy");
        } else {
            return () -> network.stopListen(sentMessageId.get().toString());
        }
    }


}
