package machine.lib.message;

import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;

public class LoadedTypedMessage<T extends Serializable> extends TypedMessage {

    private Class<T> contentClass;
    private T content;

    public LoadedTypedMessage(NetworkMessage<?> networkMessage, Class<T> contentClass) {
        super(networkMessage);
        this.contentClass = contentClass;
        this.content = this.getContent(this.contentClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Serializable> T getContent(Class<T> contentClass) {
        if(this.contentClass == contentClass){
            return (T) content;
        }
        else{
            throw new IllegalArgumentException("Content class does not match actual message content class.");
        }
    }

}
