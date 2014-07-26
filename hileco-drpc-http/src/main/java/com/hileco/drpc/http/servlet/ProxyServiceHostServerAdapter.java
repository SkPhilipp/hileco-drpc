package com.hileco.drpc.http.servlet;

import com.hileco.drpc.core.ProxyServiceHost;
import com.hileco.drpc.core.spec.MessageSender;
import com.hileco.drpc.core.stream.ArgumentsStreamer;

// TODO: implement as HttpServlet --> router, then a client for it
public class ProxyServiceHostServerAdapter extends ProxyServiceHost {

    /**
     * @param argumentsStreamer streamer to use to create message consumers for services with
     * @param messageSender     client to send procedure results to as callbacks
     */
    protected ProxyServiceHostServerAdapter(ArgumentsStreamer argumentsStreamer, MessageSender messageSender) {
        super(argumentsStreamer, messageSender);
    }

}
