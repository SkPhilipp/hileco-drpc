package machine.lib.message;

import machine.message.api.entities.NetworkMessage;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.Serializable;
import java.util.UUID;

public class TypedMessage {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private NetworkMessage<?> networkMessage;

    public TypedMessage(NetworkMessage<?> networkMessage) {
        this.networkMessage = networkMessage;
    }

    public UUID getMessageId() {
        return networkMessage.getMessageId();
    }

    public String getTopic() {
        return networkMessage.getTopic();
    }

    public <T extends Serializable> T getContent(Class<T> contentClass) {
        return OBJECT_MAPPER.convertValue(networkMessage.getContent(), contentClass);
    }

}
