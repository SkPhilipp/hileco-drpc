package com.hileco.drpc.http.server;

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
 * Logs all POST request's parsed {@link Metadata}.
 *
 * @author Philipp Gayret
 */
public class LoggingServer {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingServer.class);

    private final BasicServer basicServer;

    public LoggingServer() {
        this.basicServer = new BasicServer();
    }

    public void start(Integer port) throws Exception {

        // initialize a logging servlet and let it handle all requests
        Map<String, Servlet> servlets = new HashMap<>();
        servlets.put("/*", new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                Metadata metadata = HttpHeaderUtils.fromHeaders(req::getHeader);
                LOG.info(metadata.toString());
            }
        });
        this.basicServer.start(port, servlets);

    }

}
