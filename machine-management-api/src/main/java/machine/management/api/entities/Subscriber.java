package machine.management.api.entities;

import machine.lib.service.dao.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
