package machine.lib.client.messaging;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

public class JSONNetworkMessageSerializer implements NetworkMessageSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String serialize(NetworkMessage<?> networkMessage) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(networkMessage);
    }

    @Override
    public byte[] serializeToBytes(NetworkMessage<?> networkMessage) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(networkMessage);
    }

    @Override
    public <T extends Serializable> NetworkMessage<T> derialize(String serializedNetworkmessage, Class<NetworkMessage<T>> type) throws IOException {
        return OBJECT_MAPPER.readValue(serializedNetworkmessage, type);
    }

    @Override
    public <T extends Serializable> NetworkMessage<T> derializeFromBytes(byte[] serializedNetworkmessage, Class<NetworkMessage<T>> type) throws IOException {
        return OBJECT_MAPPER.readValue(serializedNetworkmessage, type);
    }

}
