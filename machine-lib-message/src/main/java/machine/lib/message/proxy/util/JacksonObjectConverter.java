package machine.lib.message.proxy.util;

import machine.lib.message.api.util.ObjectConverter;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Serializes and deserializes most objects, using Jackson.
 *
 * TODO: Using this, is a hit on performance - it would be better to not have to require a converter at all.
 */
public class JacksonObjectConverter implements ObjectConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <R> R convert(Object fromValue, Class<R> targetType) {
        return OBJECT_MAPPER.convertValue(fromValue, targetType);
    }

}
