package machine.management.domain;

import machine.management.services.lib.dao.Model;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Represents a subscriber to a topic, with target being an abstract reference on how to send to the subscriber.
 */
@Entity
@Table(name = "subscriber")
public class Subscriber implements Model {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "id")
    private UUID id;
    @Column(name = "topic")
    private String topic;
    @Column(name = "target")
    private String target;

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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
