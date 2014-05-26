package machine.lib.message;

import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.UUID;

public interface Network {

    public void beginListen(String topic, MessageHandler<?> messageHandler);

    public void stopListen(String topic, MessageHandler<?> messageHandler);

    public <T extends Serializable> UUID publishMessage(String topic, T content);

    public <T extends Serializable> void publishCustom(NetworkMessage<T> message);

}
