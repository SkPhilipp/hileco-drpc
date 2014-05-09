package machine.backbone.util.proxy;

/**
 * Anything that can provide an object.
 *
 * @param <T> the type of the object to provide.
 */
public interface Resolver<T> {

    /**
     * The object to call, should never return null.
     *
     * @return the actual object.
     */
    public T resolve();

}
