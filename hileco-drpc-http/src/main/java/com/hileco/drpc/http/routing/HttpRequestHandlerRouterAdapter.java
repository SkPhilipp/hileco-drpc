package com.hileco.drpc.http.routing;

import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.core.HttpHeaderUtils;
import com.hileco.drpc.http.core.HttpRequest;
import com.hileco.drpc.http.core.HttpRequestHandler;

import java.io.IOException;

/**
 * {@link HttpRouter} as an {@link HttpRequestHandler}.
 *
 * @author Philipp Gayret
 */
public class HttpRequestHandlerRouterAdapter implements HttpRequestHandler {

    private HttpRouter httpRouter;

    public HttpRequestHandlerRouterAdapter(HttpRouter httpRouter) {
        this.httpRouter = httpRouter;
    }

    public String getReplyToHost(HttpRequest httpRequest) {
        String header = httpRequest.getHeader(HttpHeaderUtils.ROUTER_REPLY_TO_HOST);
        return header != null ? header : httpRequest.getRemoteHost();
    }

    public Integer getReplyToPost(HttpRequest httpRequest) {
        String header = httpRequest.getHeader(HttpHeaderUtils.ROUTER_REPLY_TO_PORT);
        return header != null ? Integer.parseInt(header) : 8080;
    }

    @Override
    public void handle(HttpRequest httpRequest) throws IOException {
        String replyToHost = this.getReplyToHost(httpRequest);
        Integer replyToPost = this.getReplyToPost(httpRequest);
        Metadata metadata = HttpHeaderUtils.fromHeaders(httpRequest::getHeader);
        this.httpRouter.accept(replyToHost, replyToPost, metadata, httpRequest.getInputStream());
    }

}
