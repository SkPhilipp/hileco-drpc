package machine.lib.message.proxy;

import machine.lib.message.api.Network;
import machine.lib.message.api.NetworkService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemotedInvocationHandler<T extends NetworkService, P> implements InvocationHandler {

    private final boolean useBinding;
    private final P binding;
    private final Network network;
    private final Class<T> type;

    public RemotedInvocationHandler(Network network, Class<T> type, P binding) {
        this.type = type;
        this.network = network;
        this.binding = binding;
        this.useBinding = true;
    }

    public RemotedInvocationHandler(Network network, Class<T> type) {
        this.type = type;
        this.network = network;
        this.binding = null;
        this.useBinding = false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvokeMessage callMessage = new InvokeMessage();
        callMessage.setMethod(method.getName());
        callMessage.setArguments(args);
        String topic;
        if (useBinding) {
            topic = RemoteTopics.getTopic(type, binding);
        } else {
            topic = RemoteTopics.getTopic(type);
        }
        network.publishMessage(topic, callMessage);
        return null;
    }

}
