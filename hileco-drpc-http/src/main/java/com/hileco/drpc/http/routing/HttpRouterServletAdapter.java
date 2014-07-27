package com.hileco.drpc.http.routing;

import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.core.HttpHeaderUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Allows an {@link HttpRouter} to be accessed through an {@link HttpServlet}.
 *
 * @author Philipp Gayret
 */
public class HttpRouterServletAdapter extends HttpServlet {

    public static final String ROUTER_REPLY_TO_HOST = "H-ReplyToHost";
    public static final String ROUTER_REPLY_TO_PORT = "H-ReplyToPort";

    private HttpRouter httpRouter;

    public HttpRouterServletAdapter(HttpRouter httpRouter) {
        this.httpRouter = httpRouter;
    }

    public String getReplyToHost(HttpServletRequest req) {
        String header = req.getHeader(ROUTER_REPLY_TO_HOST);
        return header != null ? header : req.getRemoteHost();
    }

    public Integer getReplyToPost(HttpServletRequest req) {
        String header = req.getHeader(ROUTER_REPLY_TO_PORT);
        return header != null ? Integer.parseInt(header) : req.getRemotePort();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String replyToHost = this.getReplyToHost(req);
        Integer replyToPost = this.getReplyToPost(req);
        Metadata metadata = HttpHeaderUtils.fromHeaders(req::getHeader);
        this.httpRouter.accept(replyToHost, replyToPost, metadata, req.getInputStream());
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

}
