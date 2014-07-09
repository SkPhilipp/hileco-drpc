package com.hileco.drcp.http.api.models;

import com.hileco.drcp.core.api.models.Subscription;

public class HTTPSubscription extends Subscription {

    private String host;
    private Integer port;

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
