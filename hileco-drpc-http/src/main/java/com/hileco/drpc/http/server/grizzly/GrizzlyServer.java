package com.hileco.drpc.http.server.grizzly;

import com.hileco.drpc.http.core.HttpRequestHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * Most basic functionality for making servlets available over any given port, through Jetty.
 *
 * @author Philipp Gayret
 */
public class GrizzlyServer {

    /**
     * Starts a server which delegates to the given servlets.
     *
     * @param port               port number to listen on
     * @param httpRequestHandler request handler
     */
    public void start(Integer port, HttpRequestHandler httpRequestHandler) throws Exception {

        HttpServer server = HttpServer.createSimpleServer(null, port);
        server.getServerConfiguration().addHttpHandler(new HttpHandler() {
            public void service(Request request, Response response) throws Exception {
                HttpRequestAdapter httpRequestAdapter = new HttpRequestAdapter(request);
                httpRequestHandler.handle(httpRequestAdapter);
            }
        }, "/*");
        server.start();

    }

}