package machine.lib.service.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * For every invocation made, it gets the object via the resolver given at initialization.
 *
 * @param <T>
 */
public class ResolvingInvocationHandler<T> implements InvocationHandler {

    private final Resolver<T> resolver;

    public ResolvingInvocationHandler(Resolver<T> resolver) {
        this.resolver = resolver;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        T object = resolver.resolve();
        return method.invoke(object, args);
    }

}