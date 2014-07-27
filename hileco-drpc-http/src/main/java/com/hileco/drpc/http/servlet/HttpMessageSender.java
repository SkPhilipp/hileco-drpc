package com.hileco.drpc.http.servlet;

import com.hileco.drpc.core.spec.MessageSender;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.stream.JSONArgumentsStreamer;
import com.hileco.drpc.http.HttpHeaderUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Synchronously sends messages as HTTP requests to any given target.
 *
 * @author Philipp Gayret
 */
public class HttpMessageSender implements MessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(HttpMessageSender.class);
    private static final ArgumentsStreamer JSON_ARGUMENTS_STREAMER = new JSONArgumentsStreamer();

    public static final int DEFAULT_REQUEST_TIMEOUT = 2000;

    private final String target;
    private final HttpClient httpClient;

    /**
     * @param target url to send http requests to
     */
    public HttpMessageSender(String target) {
        this.target = target;
        RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT)
                .setConnectTimeout(DEFAULT_REQUEST_TIMEOUT)
                .setSocketTimeout(DEFAULT_REQUEST_TIMEOUT)
                .build();
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
    }

    @Override
    public void send(Metadata metadata, Object[] content) {

        HttpStreamedEntity httpStreamedEntity = new HttpStreamedEntity(JSON_ARGUMENTS_STREAMER, content);
        HttpPost request = new HttpPost(target);
        HttpHeaderUtils.writeHeaders(metadata, request::setHeader);
        request.setEntity(httpStreamedEntity);
        try {
            httpClient.execute(request);
        } catch (IOException e) {
            LOG.warn("Unable to send", e);
        }

    }

}
