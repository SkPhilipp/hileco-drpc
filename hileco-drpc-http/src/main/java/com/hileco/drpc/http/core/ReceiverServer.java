package com.hileco.drpc.http.core;

import com.hileco.drpc.core.spec.MessageReceiver;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.core.grizzly.GrizzlyServer;

import java.io.IOException;

/**
 * @author Philipp Gayret
 */
public class ReceiverServer {

    private final GrizzlyServer grizzlyServer;

    public ReceiverServer() {
        this.grizzlyServer = new GrizzlyServer();
    }

    public void start(Integer port, MessageReceiver messageReceiver) throws IOException {

        // initialize a message receiver servlet and let it handle all requests
        this.grizzlyServer.start(port, (httpRequest) -> {
            Metadata metadata = HttpHeaderUtils.fromHeaders(httpRequest::getHeader);
            messageReceiver.accept(metadata, httpRequest.getInputStream());
        });

    }

}
