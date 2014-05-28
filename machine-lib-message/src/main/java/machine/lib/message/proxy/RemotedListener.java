package machine.lib.message.proxy;

import machine.lib.message.TypedMessage;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class RemotedListener implements Consumer<TypedMessage> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(RemotedListener.class);
    private Object proxied;

    public RemotedListener(Object proxied) {
        this.proxied = proxied;
    }

    @Override
    public void accept(TypedMessage message) {
        MethodCallMessage call = message.getContent(MethodCallMessage.class);
        Method[] methods = proxied.getClass().getMethods();
        Method match = null;
        for (Method method : methods) {
            if (method.getName().equals(call.getMethod())) {
                match = method;
                break;
            }
        }
        if (match != null) {
            Class<?>[] parameterTypes = match.getParameterTypes();
            Object[] convertedArgs = new Object[parameterTypes.length];
            Object[] originalArgs = call.getArgs();
            for (int i = 0; i < parameterTypes.length; i++) {
                Object converted = OBJECT_MAPPER.convertValue(originalArgs[i], parameterTypes[i]);
                convertedArgs[i] = converted;
            }
            try {
                match.invoke(this.proxied, convertedArgs);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.error("Unable to process a call ( erred ) \"{}:{}({})\"", proxied.getClass(), call.getMethod(), call.getArgs(), e);
            }
        } else {
            LOG.error("Unable to process a call ( no match ) \"{}:{}({})\"", proxied.getClass(), call.getMethod(), call.getArgs(), new Exception());
        }
    }

}
