package machine.management.api.entities;

import machine.lib.service.api.entities.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@SuppressWarnings("unused")
@Entity
@Table(name = "server")
public class Server extends Model {

    @Column(name = "hostname")
    private String hostname;
    @Column(name = "ipaddress")
    private String ipAddress;
    @Column(name = "port")
    private Integer port;
    @Column(name = "heartbeat")
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
