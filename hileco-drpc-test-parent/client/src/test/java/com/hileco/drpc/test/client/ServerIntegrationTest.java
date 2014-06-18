package com.hileco.drpc.test.client;

import com.hileco.drpc.test.service.SampleCalculatorServer;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class ServerIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationTest.class);

    public static final int SERVICE = 8300;
    public static final int CLIENT_AND_ROUTER = 8400;

    public void localized(Runnable runnable) throws Exception {
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
    }

    @Test
    public void startAll() throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> LOG.error("Thread erred", exception));

        localized(() -> {
            Client client = new Client();
            client.start(CLIENT_AND_ROUTER);
        });

        localized(() -> {
            SampleCalculatorServer sampleCalculatorServer = new SampleCalculatorServer();
            sampleCalculatorServer.start(String.format("http://127.0.0.1:%d", CLIENT_AND_ROUTER), SERVICE);
        });

        Thread.sleep(60000);

    }

}
