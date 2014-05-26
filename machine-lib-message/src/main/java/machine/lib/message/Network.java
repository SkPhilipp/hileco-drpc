package machine.lib.message;

import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.UUID;

public interface Network {

    public void beginListen(String topic, TypedMessageHandler typedMessageHandler);

    public void stopListen(String topic, TypedMessageHandler typedMessageHandler);

    public void stopListen(String topic);

    public <T extends Serializable> UUID publishMessage(String topic, T content);

    public <T extends Serializable> UUID publishMessage(UUID topic, T content);

    public <T extends Serializable> void publishCustom(NetworkMessage<T> message);

}
