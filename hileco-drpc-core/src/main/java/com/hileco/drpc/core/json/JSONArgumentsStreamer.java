package com.hileco.drpc.core.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hileco.drpc.core.spec.ArgumentsStreamer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializes and deserializes Object arrays to and from input and output streams as JSON arrays.
 *
 * @author Philipp Gayret
 */
public class JSONArgumentsStreamer implements ArgumentsStreamer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JsonFactory FACTORY = OBJECT_MAPPER.getFactory();

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
    public Object[] deserializeFrom(InputStream argsStream, Class<?>[] elementTypes) throws IOException {
        Object[] results = new Object[elementTypes.length];
        JsonParser parser = FACTORY.createParser(argsStream);
        int index = 0;
        if (parser.nextToken() == JsonToken.START_ARRAY) {
            parser.clearCurrentToken();
            while (index < elementTypes.length) {
                results[index] = parser.readValueAs(elementTypes[index]);
                index++;
            }
        }
        return results;
    }

    /**
     * Serializes given arguments as a JSON array and while serializing writes it to the given outputStream.
     * <p/>
     * Does not close the stream.
     *
     * @param outputStream stream to write JSON array to
     * @param arguments    serializable objects to be written
     */
    public void serializeTo(OutputStream outputStream, Object[] arguments) throws IOException {
        JsonGenerator jsonGenerator = FACTORY.createGenerator(outputStream);
        jsonGenerator.writeStartArray();
        if (arguments != null) {
            for (Object arg : arguments) {
                jsonGenerator.writeObject(arg);
            }
        }
        jsonGenerator.writeEndArray();
    }

}
