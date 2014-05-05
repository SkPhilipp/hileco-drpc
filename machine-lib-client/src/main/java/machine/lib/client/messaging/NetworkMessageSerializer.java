package machine.lib.client.messaging;

import com.google.common.base.Charsets;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

public class NetworkMessageSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static Charset CHARSET = Charsets.UTF_8;

    public String serialize(NetworkMessage<?> networkMessage) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(networkMessage);
    }

    public byte[] serializeToBytes(NetworkMessage<?> networkMessage) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(networkMessage);
    }

    public <T extends Serializable> NetworkMessage<T> derialize(String serializedNetworkmessage, Class<NetworkMessage<T>> type) throws IOException {
        return OBJECT_MAPPER.readValue(serializedNetworkmessage, type);
    }

    public <T extends Serializable> NetworkMessage<T> derializeFromBytes(byte[] serializedNetworkmessage, Class<NetworkMessage<T>> type) throws IOException {
        return OBJECT_MAPPER.readValue(serializedNetworkmessage, type);
    }

}
