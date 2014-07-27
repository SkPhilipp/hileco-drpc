package com.hileco.drpc.http.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class BasicServer {

    public static final String CONTEXT_PATH = "/";
    public static final String PATH_SPEC = "/*";
    private final int port;

    public BasicServer(int port) {
        this.port = port;
    }

    /**
     * Starts a Jetty server, hosting servlets on port {@link #port}.
     */
    public void start() throws Exception {

        // servlet context handler for services
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath(CONTEXT_PATH);
        ServletHolder servletHolder = new ServletHolder();
        servletContextHandler.addServlet(servletHolder, PATH_SPEC);
        Server server = new Server(port);
        server.setHandler(servletContextHandler);
        server.start();

    }

}