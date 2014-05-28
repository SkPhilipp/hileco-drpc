package machine.lib.message.api;

import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

public interface Network {

    public Consumer<NetworkMessage> beginListen(String topic, Consumer<NetworkMessage> typedMessageHandler);

    public void stopListen(String topic, Consumer<NetworkMessage> typedMessageHandler);

    public void stopListen(String topic);

    public <T extends Serializable> UUID publishMessage(String topic, T content);

    public <T extends Serializable> void publishCustom(NetworkMessage<T> message);

}
