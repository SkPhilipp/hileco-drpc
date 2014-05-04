package machine.management.domain;

import machine.management.services.lib.dao.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents an message for a given topic.
 */
@Entity
@Table(name = "message")
public class Message extends Model {

    @Column(name = "topic")
    private String topic;
    @Column(name = "content")
    private byte[] content;
    @Column(name = "timestamp")
    private Long timestamp;

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
