package machine.router.api.entities;

import java.io.Serializable;
import java.util.UUID;

/**
 * A message consisting of any content and a topic.
 */
public class NetworkMessage implements Serializable {

    private UUID messageId;
    private String topic;
    private String method;
    private Object[] arguments;

    /**
     * Creates the message with a given topic and content, and randomly assigns a messageId.
     *
     * @param topic value for {@link #topic}
     */
    public NetworkMessage(String topic, String method, Object[] arguments) {
        this.method = method;
        this.arguments = arguments;
        this.messageId = UUID.randomUUID();
        this.topic = topic;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

}
