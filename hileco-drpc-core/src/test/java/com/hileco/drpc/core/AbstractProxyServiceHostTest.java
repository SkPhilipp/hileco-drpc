package com.hileco.drpc.core;

import com.hileco.drpc.core.spec.MessageSender;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.stream.JSONArgumentsStreamer;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Philipp Gayret
 */
public class AbstractProxyServiceHostTest {

    public static interface SampleService {

        public void add(Integer a, Integer b);

    }

    /**
     * Tests connector functionality where a call should translate to a send on a messageSender.
     */
    @Test
    public void test() {

        MessageSender messageSender = Mockito.mock(MessageSender.class);
        ArgumentsStreamer argumentsStreamer = new JSONArgumentsStreamer();
        AbstractProxyServiceHost abstractProxyServiceHost = new AbstractProxyServiceHost(argumentsStreamer) {

            @Override
            public void send(Metadata metadata, Object[] content) {
                messageSender.send(metadata, content);
            }

        };

        ServiceConnector<SampleService> serviceConnector = abstractProxyServiceHost.connector(SampleService.class);
        SampleService sampleService = serviceConnector.connect("1");

        sampleService.add(1, 2);

        Mockito.verify(messageSender, Mockito.times(1)).send(Mockito.any(), Mockito.any());

    }

}
