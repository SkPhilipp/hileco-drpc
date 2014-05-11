package machine.lib.message;

import machine.message.api.entities.NetworkMessage;

public interface CallbackHandler {

    public void handle(NetworkMessage<?> message);

}
