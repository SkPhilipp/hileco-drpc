package com.hileco.drpc.test.client;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class ServerIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationTest.class);

    public static final int PORT_SERVICE = 8300;
    public static final int PORT_ROUTER = 8500;
    public static final int PORT_CLIENT = 8400;

    @Test
    public void startAll() throws Exception {

        // TODO: Start a local router
        // TODO: Start up service project's main and give router url
        // TODO: Start up client project's main and give router url

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> LOG.error("Thread erred", exception));

    }

}
