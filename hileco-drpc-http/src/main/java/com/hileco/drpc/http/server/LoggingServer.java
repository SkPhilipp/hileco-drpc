package com.hileco.drpc.http.server;

import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.core.HttpHeaderUtils;
import com.hileco.drpc.http.server.grizzly.GrizzlyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs all POST request's parsed {@link Metadata}.
 *
 * @author Philipp Gayret
 */
public class LoggingServer {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingServer.class);

    private final GrizzlyServer grizzlyServer;

    public LoggingServer() {
        this.grizzlyServer = new GrizzlyServer();
    }

    public void start(Integer port) throws Exception {

        // initialize a logging servlet and let it handle all requests
        this.grizzlyServer.start(port, (httpRequest) -> {
            Metadata metadata = HttpHeaderUtils.fromHeaders(httpRequest::getHeader);
            LOG.info(metadata.toString());
        });

    }

}
