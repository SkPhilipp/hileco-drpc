package machine.management.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Server")
public class Server implements Model {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    @Column(name = "hostname")
    private String hostname;
    @Column(name = "ipAddress")
    private String ipAddress;
    @Column(name = "port")
    private Integer port;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

}
