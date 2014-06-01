package machine.router.api.entities;

import java.io.Serializable;
import java.util.UUID;

/**
 * A message consisting of any content and a topic.
 */
public class NetworkMessage implements Serializable {

    private UUID messageId;
    private String topic;
    private Object content;

    /**
     * Creates the message with a given topic and content, and randomly assigns a messageId.
     *
     * @param topic value for {@link #topic}
     * @param content value for {@link #content}
     */
    public NetworkMessage(String topic, Object content) {
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

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

}
