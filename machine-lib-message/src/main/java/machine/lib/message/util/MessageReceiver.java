package machine.lib.message.util;

import machine.message.api.entities.NetworkMessage;

public interface MessageReceiver<RES> {

    public void handle(NetworkMessage<?> networkMessage, RES content);

}
