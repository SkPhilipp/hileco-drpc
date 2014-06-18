package machine.drcp.core.client;

/**
 * Object converter, useable to convert incoming data to the correct type.
 *
 * TODO: Ideally the JSON would be converted to the correct data type by streaming {@link machine.drcp.core.api.models.RPC#params}
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
