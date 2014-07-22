package com.hileco.drcp.core.client;

/**
 * Object converter, useable to convert incoming data to the correct type.
 */
public interface ObjectConverter {

    /**
     * An ObjectConverter which returns the source object as the given target class, does not perform any converting.
     */
    public static final ObjectConverter RAW = new ObjectConverter() {
            @Override
            public <R> R convert(Object source, Class<R> target) throws IllegalArgumentException {
                return (R) source;
            }
        };
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
