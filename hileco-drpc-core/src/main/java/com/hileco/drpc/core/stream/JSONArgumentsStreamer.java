package com.hileco.drpc.core.stream;

import com.hileco.drpc.core.stream.ArgumentsStreamer;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializes and deserializes Object arrays to and from input and output streams as JSON arrays.
 *
 * @author Philipp Gayret
 */
public class JSONArgumentsStreamer implements ArgumentsStreamer {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonFactory factory = objectMapper.getJsonFactory();

    /**
     * Expects the given argsStream to return a JSON array containing objects parseable as the given element types.
     * <p/>
     * Amount of elements from stream must equal amount of classes.
     * <p/>
     * Does not close the stream.
     *
     * @param argsStream   stream to a JSON array
     * @param elementTypes classes to parse the elements as
     * @return instantiated objects
     */
    public Object[] deserializeFrom(InputStream argsStream, Class<?>[] elementTypes) {
        try {
            Object[] results = new Object[elementTypes.length];
            JsonParser parser = factory.createJsonParser(argsStream);
            int index = 0;
            if (parser.nextToken() == JsonToken.START_ARRAY) {
                parser.clearCurrentToken();
                while (index < elementTypes.length) {
                    results[index] = parser.readValueAs(elementTypes[index]);
                    index++;
                }
            }
            return results;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse input as given class list", e);
        }
    }

    /**
     * Serializes given arguments as a JSON array and while serializing writes it to the given outputStream.
     * <p/>
     * Does not close the stream.
     *
     * @param outputStream stream to write JSON array to
     * @param arguments    serializable objects to be written
     */
    public void serializeTo(OutputStream outputStream, Object[] arguments) {
        try {
            JsonGenerator jsonGenerator = factory.createJsonGenerator(outputStream);
            jsonGenerator.writeStartArray();
            for (Object arg : arguments) {
                jsonGenerator.writeObject(arg);
            }
            jsonGenerator.writeEndArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to write arguments to stream", e);
        }
    }

}