package com.hileco.drpc.http.core;

import com.hileco.drpc.core.spec.MessageSender;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.spec.ArgumentsStreamer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Synchronously sends messages as HTTP requests to any given target.
 *
 * @author Philipp Gayret
 */
public class HttpClientMessageSender implements MessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientMessageSender.class);

    private final ArgumentsStreamer argumentsStreamer;
    private final String listeningHost;
    private final Integer listeningPort;
    private final String target;
    private final HttpClient httpClient;

    /**
     * @param httpClient        http client to send with
     * @param argumentsStreamer streamer to serialize content with
     * @param target            url to send http requests to
     * @param listeningHost     hostname for receiver to send replies back to
     * @param listeningPort     listeningHost's port
     */
    public HttpClientMessageSender(HttpClient httpClient, ArgumentsStreamer argumentsStreamer, String target, String listeningHost, Integer listeningPort) {
        this.httpClient = httpClient;
        this.argumentsStreamer = argumentsStreamer;
        this.target = target;
        this.listeningHost = listeningHost;
        this.listeningPort = listeningPort;
    }

    @Override
    public void send(Metadata metadata, Object[] content) {

        HttpStreamedEntity httpStreamedEntity = new HttpStreamedEntity(argumentsStreamer, content);
        HttpPost request = new HttpPost(target);
        HttpHeaderUtils.writeHeaders(metadata, request::setHeader);
        request.setHeader(HttpHeaderUtils.ROUTER_REPLY_TO_HOST, this.listeningHost);
        request.setHeader(HttpHeaderUtils.ROUTER_REPLY_TO_PORT, this.listeningPort.toString());
        request.setEntity(httpStreamedEntity);
        try {
            HttpResponse response = httpClient.execute(request);
            response.getEntity().getContent().close();
        } catch (IOException e) {
            LOG.warn("Unable to send", e);
        }

    }

}
