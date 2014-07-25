package com.hileco.drpc.core;

import com.hileco.drpc.core.spec.MessageClient;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.stream.JSONArgumentsStreamer;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;

/**
 * Tests functionality of serialization and deserialization from the perspective of an end-user of the core library.
 *
 * @author Philipp Gayret
 */
public class OutgoingIncomingTest {

    public static final int ITERATIONS = 5000;

    public static interface SampleService {

        public Integer add(Integer a, Integer b);

    }

    /**
     * More or less a speed test for serializing and deserializing.
     * <p/>
     * On any machine it must take less than 1ms total for:
     * <p/>
     * - Creating metadata and extracting invocation arguments
     * - Serializing invocation arguments
     * - Deserializing invocation arguments
     * - Matching the metadata to a method and invoking it
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = ITERATIONS)
    public void testRPCValue() throws Exception {

        MessageClient messageClient = Mockito.mock(MessageClient.class);
        SampleService mockService = Mockito.mock(SampleService.class);

        // we'll use the JSON argument streamer implementation
        ArgumentsStreamer argumentsStreamer = new JSONArgumentsStreamer();

        // create a construction where we basically connect an outgoingmessageproducer to an incomingmessageproducer, so
        // that any calls made on the outgoingMP's proxy object go through the serialization and deserialization process
        // and end up at the mock service defined.
        ProxyMessageConsumer delegatingMessageStreamConsumer = new ProxyMessageConsumer(argumentsStreamer, messageClient, mockService);

        SampleService proxyService = (SampleService) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{SampleService.class}, (proxy, method, arguments) -> {
            Metadata metadata = new Metadata(null, SampleService.class.getName(), method.getName());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            argumentsStreamer.serializeTo(byteArrayOutputStream, arguments);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            delegatingMessageStreamConsumer.accept(metadata, byteArrayInputStream);
            byteArrayOutputStream.close();
            byteArrayInputStream.close();
            return null;
        });

        for (int i = 0; i < ITERATIONS; i++) {
            proxyService.add(1, 2);
        }

        // verify the call was made on the service given to delegatingMessageStreamConsumer
        Mockito.verify(mockService, Mockito.times(ITERATIONS)).add(1, 2);

    }

}
