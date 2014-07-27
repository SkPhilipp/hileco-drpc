package com.hileco.drpc.http.server;

import com.hileco.drpc.core.spec.MessageReceiver;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.core.HttpHeaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Philipp Gayret
 */
public class MessageReceiverServer {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingServer.class);

    /**
     * {@link MessageReceiver} as a {@link HttpServlet}.
     */
    public static class MessageReceiverServlet extends HttpServlet {

        private MessageReceiver messageReceiver;

        public MessageReceiverServlet(MessageReceiver messageReceiver) {
            this.messageReceiver = messageReceiver;
        }

        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            Metadata metadata = HttpHeaderUtils.fromHeaders(req::getHeader);
            this.messageReceiver.accept(metadata, req.getInputStream());
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

    }

    private final BasicServer basicServer;

    public MessageReceiverServer() {
        this.basicServer = new BasicServer();
    }

    public void start(Integer port, MessageReceiver messageReceiver) throws Exception {

        // initialize a message receiver servlet and let it handle all requests
        HttpServlet httpServlet = new MessageReceiverServlet(messageReceiver);
        Map<String, Servlet> servlets = new HashMap<>();
        servlets.put("/*", httpServlet);
        this.basicServer.start(port, servlets);

    }


}
