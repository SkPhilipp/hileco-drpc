package com.hileco.drpc.http.subscription;

import java.io.Serializable;
import java.util.UUID;

/**
 * A subscription containing a unique id, message topic subscription, and transport information.
 *
 * @author Philipp Gayret
 */
public class Subscription implements Serializable {

    private UUID id;
    private String topic;
    private String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
