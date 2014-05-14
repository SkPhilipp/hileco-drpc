package machine.management.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@SuppressWarnings("unused")
@Entity
@Table(name = Server.SERVER)
public class Server extends Model {

    public static final String HOSTNAME = "hostname";
    public static final String SERVER = "server";
    public static final String IPADDRESS = "ipaddress";
    public static final String PORT = "port";
    public static final String HEARTBEAT = "heartbeat";

    @Column(name = HOSTNAME)
    private String hostname;
    @Column(name = IPADDRESS)
    private String ipAddress;
    @Column(name = PORT)
    private Integer port;
    @Column(name = HEARTBEAT)
    private Date heartbeat;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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

    public Date getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Date heartbeat) {
        this.heartbeat = heartbeat;
    }

}
