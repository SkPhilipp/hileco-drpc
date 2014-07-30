package com.hileco.drpc.http.core.grizzly;

import com.hileco.drpc.http.core.HttpRequest;
import org.glassfish.grizzly.http.server.Request;

import java.io.InputStream;

/**
 * {@link Request} as an {@link HttpRequest}
 *
 * @author Philipp Gayret
 */
public class HttpRequestAdapter implements HttpRequest {

    private Request request;

    public HttpRequestAdapter(Request request) {
        this.request = request;
    }

    @Override
    public InputStream getInputStream() {
        return request.getInputStream();
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

}
