package machine.management.api.receiver.entities;

import java.io.Serializable;

/**
 * A message consisting of any content and a topic.
 *
 * @param <T> any type of content
 */
@SuppressWarnings("unused")
public class NetworkMessage<T extends Serializable> {

    private String topic;
    private T content;

    public NetworkMessage(String topic, T content) {
        this.topic = topic;
        this.content = content;
    }

    public NetworkMessage() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

}
