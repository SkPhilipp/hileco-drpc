package machine.drcp.core.routing;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Subscription implements Serializable {

    private UUID id;
    private String topic;
    private String host;
    private Integer port;
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

}
