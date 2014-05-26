package machine.lib.message.util;

import machine.message.api.entities.NetworkMessage;

public interface MessageProcessor<RES> {

    public void process(NetworkMessage<?> networkMessage, RES content);

}
