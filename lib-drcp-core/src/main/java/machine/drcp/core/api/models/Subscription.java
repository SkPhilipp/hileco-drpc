package machine.drcp.core.api.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Subscription implements Serializable {

    private UUID id;
    private String topic;
    private Date expires;

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

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

}
