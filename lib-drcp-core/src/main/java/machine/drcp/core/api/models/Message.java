package machine.drcp.core.api.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * A message consisting of any content and a topic.
 */
public class Message<T> implements Serializable {

    private UUID id;
    private String topic;
    private T content;

    /**
     * Creates the message with a given topic and content, and randomly assigns an id.
     *
     * @param topic   value for {@link #topic}
     * @param content value for {@link #content}
     */
    public Message(String topic, T content) {
        this.id = UUID.randomUUID();
        this.topic = topic;
        this.content = content;
    }

    public Message() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
