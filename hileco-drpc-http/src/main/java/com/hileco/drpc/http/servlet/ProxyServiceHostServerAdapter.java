package com.hileco.drpc.http.servlet;

import com.hileco.drpc.core.ProxyServiceHost;
import com.hileco.drpc.core.spec.OutgoingMessageConsumer;
import com.hileco.drpc.core.stream.ArgumentsStreamer;

// TODO: implement as HttpServlet

public class ProxyServiceHostServerAdapter extends ProxyServiceHost {

    /**
     * @param argumentsStreamer streamer to use to create message consumers for services with
     * @param outgoingMessageConsumer     client to send procedure results to as callbacks
     */
    protected ProxyServiceHostServerAdapter(ArgumentsStreamer argumentsStreamer, OutgoingMessageConsumer outgoingMessageConsumer) {
        super(argumentsStreamer, outgoingMessageConsumer);
    }

}
