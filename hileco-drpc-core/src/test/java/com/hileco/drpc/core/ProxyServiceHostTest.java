package com.hileco.drpc.core;

import com.hileco.drpc.core.spec.MessageSender;
import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.stream.JSONArgumentsStreamer;
import org.junit.Test;
import org.mockito.Mockito;

public class ProxyServiceHostTest {

    public static interface SampleService {

        public void add(Integer a, Integer b);

    }

    @Test
    public void test() {

        MessageSender messageSender = Mockito.mock(MessageSender.class);
        ArgumentsStreamer argumentsStreamer = new JSONArgumentsStreamer();
        ProxyServiceHost proxyServiceHost = new ProxyServiceHost(argumentsStreamer, messageSender);

        ServiceConnector<SampleService> serviceConnector = proxyServiceHost.connector(SampleService.class);
        SampleService sampleService = serviceConnector.connect("1");

        sampleService.add(1, 2);

        Mockito.verify(messageSender, Mockito.times(1)).publish(Mockito.any(), Mockito.any());

    }

}
