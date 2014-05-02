package machine.management.model;

import machine.services.lib.model.Model;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Represents an event for a given topic.
 */
@Entity
@Table(name = "event")
public class Event implements Model {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "id")
    private UUID id;
    @Column(name = "topic")
    private String topic;
    @Column(name = "content")
    private byte[] content;
    @Column(name = "timestamp")
    private Long timestamp;

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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
