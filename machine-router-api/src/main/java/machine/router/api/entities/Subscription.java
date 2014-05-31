package machine.router.api.entities;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = Subscription.SUBSCRIPTION)
public class Subscription implements Serializable {

    public static final String ID = "id";
    public static final String EXPIRES = "expires";
    public static final String PORT = "port";
    public static final String IPADDRESS = "ipaddress";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIPTION = "subscription";
    @Id
    @Type(type = "uuid-char")
    @Column(name = ID)
    private UUID id;
    @Column(name = TOPIC)
    private String topic;
    @Column(name = IPADDRESS)
    private String host;
    @Column(name = PORT)
    private Integer port;
    @Column(name = EXPIRES)
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
