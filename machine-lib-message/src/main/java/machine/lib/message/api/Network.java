package machine.lib.message.api;

import machine.lib.message.TypedMessage;
import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

public interface Network {

    public Consumer<TypedMessage> beginListen(String topic, Consumer<TypedMessage> typedMessageHandler);

    public void stopListen(String topic, Consumer<TypedMessage> typedMessageHandler);

    public void stopListen(String topic);

    public <T extends Serializable> UUID publishMessage(String topic, T content);

    public <T extends Serializable> void publishCustom(NetworkMessage<T> message);

}
