package machine.backbone.util.proxy;

import java.lang.reflect.Proxy;

public class ResolvingProxyFactory {

    private ClassLoader classLoader = this.getClass().getClassLoader();

    /**
     * Creates a new resolving proxy, using the given resolver.
     *
     * @param resolver object resolver
     * @param iface interface the proxy should implement
     * @param <T> type to return
     * @return a proxy
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> iface, Resolver<T> resolver) {
        ResolvingInvocationHandler handler = new ResolvingInvocationHandler(resolver);
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{iface}, handler);
    }

}
