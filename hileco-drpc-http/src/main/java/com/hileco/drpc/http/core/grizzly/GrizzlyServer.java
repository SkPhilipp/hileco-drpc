package com.hileco.drpc.http.core.grizzly;

import com.hileco.drpc.http.core.HttpRequestHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;

/**
 * Most basic functionality for making servlets available over any given port, through Jetty.
 *
 * @author Philipp Gayret
 */
public class GrizzlyServer {

    private HttpServer server;

    /**
     * Starts a server which delegates requests to the given servlets.
     *
     * @param port               port number to listen on
     * @param httpRequestHandler request handler
     */
    public void start(Integer port, HttpRequestHandler httpRequestHandler) throws IOException {

        server = HttpServer.createSimpleServer(null, port);
        server.getServerConfiguration().addHttpHandler(new HttpHandler() {
            public void service(Request request, Response response) throws Exception {
                HttpRequestAdapter httpRequestAdapter = new HttpRequestAdapter(request);
                httpRequestHandler.handle(httpRequestAdapter);
            }
        }, "/*");
        server.start();

    }

    /**
     * Immediately shuts down the server.
     */
    public void shutdownNow() {
        server.shutdownNow();
    }

}