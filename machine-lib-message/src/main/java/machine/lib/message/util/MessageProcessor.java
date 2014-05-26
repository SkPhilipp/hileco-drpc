package machine.lib.message.util;

import machine.lib.message.TypedMessage;

public interface MessageProcessor<RES> {

    public void process(TypedMessage typedMessage, RES content);

}
