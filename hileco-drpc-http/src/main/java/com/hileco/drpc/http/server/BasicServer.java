package com.hileco.drpc.http.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;
import java.util.Map;

/**
 * Most basic functionality for making servlets available over any given port, through Jetty.
 *
 * @author Philipp Gayret
 */
public class BasicServer {

    public static final String CONTEXT_PATH = "/";

    /**
     * Starts a server which delegates to the given servlets.
     *
     * @param port     port number to listen on
     * @param servlets map containing path specs and servlets to publish
     * @throws Exception
     */
    public void start(Integer port, Map<String, Servlet> servlets) throws Exception {

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath(CONTEXT_PATH);
        for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
            ServletHolder servletHolder = new ServletHolder(entry.getValue());
            servletContextHandler.addServlet(servletHolder, entry.getKey());
        }
        Server server = new Server(port);
        server.setHandler(servletContextHandler);
        server.start();

    }

}