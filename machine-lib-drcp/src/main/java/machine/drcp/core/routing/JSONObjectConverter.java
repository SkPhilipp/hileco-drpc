package machine.drcp.core.routing;

import machine.drcp.core.routing.proxy.ObjectConverter;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Serializes and deserializes most objects, using Jackson.
 */
public class JSONObjectConverter implements ObjectConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <R> R convert(Object fromValue, Class<R> targetType) {
        return OBJECT_MAPPER.convertValue(fromValue, targetType);
    }

}
