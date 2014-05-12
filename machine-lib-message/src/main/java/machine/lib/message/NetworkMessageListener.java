package machine.lib.message;

import com.google.common.reflect.TypeToken;
import machine.message.api.entities.NetworkMessage;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.Serializable;

public abstract class NetworkMessageListener<K extends Serializable> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Class<? super K> messageType;

    public NetworkMessageListener(){
        TypeToken<K> typeToken = new TypeToken<K>(this.getClass()) {};
        this.messageType = typeToken.getRawType();
    }

    public abstract void handle(NetworkMessage<?> message);

    @SuppressWarnings("unchecked")
    public K open(NetworkMessage<?> message){
        return OBJECT_MAPPER.convertValue(message.getContent(), (Class<K>) this.messageType);
    }

}
