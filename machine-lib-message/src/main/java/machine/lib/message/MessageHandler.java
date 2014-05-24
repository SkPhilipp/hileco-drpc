package machine.lib.message;

import com.google.common.reflect.TypeToken;
import machine.message.api.entities.NetworkMessage;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.Serializable;

public abstract class MessageHandler<T extends Serializable> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Class<? super T> messageType;

    public MessageHandler(){
        TypeToken<T> typeToken = new TypeToken<T>(this.getClass()) {};
        this.messageType = typeToken.getRawType();
    }

    public abstract void handle(NetworkMessage<?> message);

    @SuppressWarnings("unchecked")
    public T open(NetworkMessage<?> message){
        return OBJECT_MAPPER.convertValue(message.getContent(), (Class<T>) this.messageType);
    }

}
