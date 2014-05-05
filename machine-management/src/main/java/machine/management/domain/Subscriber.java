package machine.management.domain;

import machine.lib.service.dao.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a subscriber to a topic, with target being an abstract reference on how to send to the subscriber.
 */
@Entity
@Table(name = "subscriber")
public class Subscriber extends Model {

    @Column(name = "topic")
    private String topic;
    @Column(name = "target")
    private String target;

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
