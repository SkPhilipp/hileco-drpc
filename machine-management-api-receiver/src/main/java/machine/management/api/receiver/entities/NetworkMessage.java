package machine.management.api.receiver.entities;

import java.io.Serializable;
import java.util.UUID;

/**
 * A message consisting of any content and a topic.
 *
 * @param <T> any type of content
 */
@SuppressWarnings("unused")
public class NetworkMessage<T extends Serializable> {

    private UUID messageId;
    private String topic;
    private T content;

    /**
     * Creates the message with a given topic and content, and randomly assigns a messageId.
     *
     * @param topic value for {@link #topic}
     * @param content value for {@link #content}
     */
    public NetworkMessage(String topic, T content) {
        this.messageId = UUID.randomUUID();
        this.topic = topic;
        this.content = content;
    }

    public NetworkMessage() {
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
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
