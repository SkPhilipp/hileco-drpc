package machine.management.domain;

import machine.lib.service.dao.Model;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Represents an event for a topic, to be sent to a subscriber.
 */
@Entity
@Table(name = "event")
public class Event extends Model {

    @Column(name = "target")
    private String target;
    @Type(type = "uuid-char")
    @Column(name = "message")
    private UUID message;
    @Column(name = "timestamp")
    private Long timestamp;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public UUID getMessage() {
        return message;
    }

    public void setMessage(UUID message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}