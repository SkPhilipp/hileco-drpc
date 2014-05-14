package machine.management.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@SuppressWarnings("unused")
@Entity
@Table(name = Subscription.SUBSCRIPTION)
public class Subscription extends Model {

    public static final String EXPIRES = "expires";
    public static final String PORT = "port";
    public static final String IPADDRESS = "ipaddress";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIPTION = "subscription";

    @Column(name = TOPIC)
    private String topic;
    @Column(name = IPADDRESS)
    private String ipAddress;
    @Column(name = PORT)
    private Integer port;
    @Column(name = EXPIRES)
    private Date expires;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
