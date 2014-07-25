package com.hileco.drpc.http.routing.spec;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.UUID;

/**
 * A subscription containing a unique id, message topic subscription, and transport information.
 *
 * @author Philipp Gayret
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription implements Serializable {

    private UUID id;
    private String topic;
    private String host;
    private Integer port;

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

}
