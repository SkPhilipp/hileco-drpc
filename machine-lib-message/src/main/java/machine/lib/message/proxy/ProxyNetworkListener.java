package machine.lib.message.proxy;

import machine.lib.message.api.Network;
import machine.lib.message.api.util.ObjectConverter;
import machine.router.api.entities.NetworkInvocationMessage;
import machine.router.api.entities.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class ProxyNetworkListener implements Consumer<NetworkMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyNetworkListener.class);
    private Network network;
    private Object proxied;
    private ObjectConverter objectConverter;

    public ProxyNetworkListener(Network network, Object proxied, ObjectConverter objectConverter) {
        this.network = network;
        this.proxied = proxied;
        this.objectConverter = objectConverter;
    }

    @Override
    public void accept(NetworkMessage message) {
        NetworkInvocationMessage invocationMessage = objectConverter.convert(message.getContent(), NetworkInvocationMessage.class);
        Method[] methods = proxied.getClass().getMethods();
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
                Object result = match.invoke(this.proxied, convertedArgs);
                if(invocationMessage.isReport()){
                    NetworkMessage reportMessage = new NetworkMessage(message.getMessageId().toString(), result);
                    network.publishMessage(reportMessage);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.error("Unable to process a call ( erred ) \"{}:{}({})\"", proxied.getClass(), invocationMessage.getMethod(), invocationMessage.getArguments(), e);
            }
        } else {
            LOG.error("Unable to process a call ( no match ) \"{}:{}({})\"", proxied.getClass(), invocationMessage.getMethod(), invocationMessage.getArguments(), new Exception());
        }
    }

}
