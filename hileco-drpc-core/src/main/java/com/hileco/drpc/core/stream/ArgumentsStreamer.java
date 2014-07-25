package com.hileco.drpc.core.stream;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Specification for arguments ( any serializable objects ) list serializing and deserializing.
 *
 * @author Philipp Gayret
 */
public interface ArgumentsStreamer {

    /**
     * Converts a byte stream to an array of objects of type of the given elementTypes.
     * <p/>
     * Does not close the stream.
     *
     * @param argsStream   stream to a JSON array
     * @param elementTypes classes to parse the elements as
     * @return instantiated objects
     * @throws IllegalArgumentException on deserialization errors
     */
    public Object[] deserializeFrom(InputStream argsStream, Class<?>[] elementTypes) throws IllegalArgumentException;

    /**
     * Converts an array of objects to bytes, bytes are written to the given outputStream.
     * <p/>
     * Does not close the stream.
     *
     * @param outputStream stream to write JSON array to
     * @param arguments    serializable objects to be written
     * @throws IllegalArgumentException on deserialization errors
     */
    public void serializeTo(OutputStream outputStream, Object[] arguments);

}
