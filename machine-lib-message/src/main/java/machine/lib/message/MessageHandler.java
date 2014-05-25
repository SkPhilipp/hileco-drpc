package machine.lib.message;

import machine.message.api.entities.NetworkMessage;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.Serializable;

public abstract class MessageHandler<T extends Serializable> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public abstract void handle(NetworkMessage<?> message);

    @SuppressWarnings("unchecked")
    public T open(NetworkMessage<?> message, Class<T> messageType){
        return OBJECT_MAPPER.convertValue(message.getContent(), messageType);
    }

}
