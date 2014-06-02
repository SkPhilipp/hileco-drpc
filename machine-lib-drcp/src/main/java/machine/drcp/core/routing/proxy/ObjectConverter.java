package machine.drcp.core.routing.proxy;

/**
 * Object converter, useable to convert incoming data to the correct type.
 *
 * TODO: Using this, is a slight hit on performance - nothing compared to network overhead though, but it would be better to not have to require a converter at all.
 */
public interface ObjectConverter {

    /**
     * Safely converts any object to any other type.
     *
     * @param source the object to be converted
     * @param target the target type's class
     * @param <R>    the target type
     * @return the converted object
     * @throws IllegalArgumentException on any error
     */
    public <R> R convert(Object source, Class<R> target) throws IllegalArgumentException;

}
