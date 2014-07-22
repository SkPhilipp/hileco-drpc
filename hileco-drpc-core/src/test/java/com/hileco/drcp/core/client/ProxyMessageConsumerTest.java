package com.hileco.drcp.core.client;

import com.hileco.drcp.core.api.MessageClient;
import com.hileco.drcp.core.api.models.Message;
import com.hileco.drcp.core.api.models.RPC;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tests functionality of the ProxyMessageConsumer on invoking services it delegates to via RPC messages.
 *
 * @author Philipp Gayret
 */
public class ProxyMessageConsumerTest {

    public static interface SampleService {

        public Integer add(Integer a, Integer b);

    }

    public static interface VoidService {

        public void add(Integer a, Integer b);

    }

    /**
     * Performs an actual RPC Message invocation on a ProxyMessageConsumer using a test message client and a test service.
     * This test verifies that the consumer can successfully convert an RPC Message to an actual Java call on the service,
     * and properly returns a response through the given MessageClient.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRPCValue() throws Exception {

        // a list to store expected rpc-replies made on the message client
        Map<UUID, Message<?>> replies = new HashMap<>();

        // a message client which stores the message and returns a randomized key when called
        MessageClient messageClient = (message) -> {
            UUID messageId = UUID.randomUUID();
            replies.put(messageId, message);
            return messageId;
        };

        // a service for the proxy message consumer to call
        SampleService sampleService = (a, b) -> a + b;

        // an actual ProxyMessageConsumer to so send an RPC Message to which should invoke the SampleService
        ProxyMessageConsumer proxyMessageConsumer = new ProxyMessageConsumer(messageClient, sampleService, ObjectConverter.RAW);

        // at this point we have everything set up to perform actual RPC Message calls, we'll be invoking the service as `#add(1, 2)`
        RPC addRPC = new RPC("add", new Object[]{1, 2});
        Message<RPC> addRPCMessage = new Message<>("anything", addRPC);
        proxyMessageConsumer.accept(addRPCMessage);

        // after processing by the proxyMessageConsumer, we should have a reply containing the `#add(1, 2)` result
        Assert.assertEquals(replies.size(), 1);
        Message<Integer> reply = (Message<Integer>) replies.values().iterator().next();
        Assert.assertEquals(reply.getContent(), sampleService.add(1, 2));

    }

    /**
     * Performs an actual RPC Message invocation on a ProxyMessageConsumer using a test message client and a test service.
     * This test verifies that the consumer can successfully convert an RPC Message to an actual Java call on the service,
     * and properly returns no responses through the given MessageClient for void methods.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRPCVoid() throws Exception {

        // a list to store expected rpc-replies made on the message client
        Map<UUID, Message<?>> replies = new HashMap<>();

        // a message client which stores the message and returns a randomized key when called
        MessageClient messageClient = (message) -> {
            UUID messageId = UUID.randomUUID();
            replies.put(messageId, message);
            return messageId;
        };

        // a service for the proxy message consumer to call
        VoidService voidService = (a, b) -> {
        };

        // an actual ProxyMessageConsumer to so send an RPC Message to which should invoke the VoidService
        ProxyMessageConsumer proxyMessageConsumer = new ProxyMessageConsumer(messageClient, voidService, ObjectConverter.RAW);

        // at this point we have everything set up to perform actual RPC Message calls, we'll be invoking the service as `#add(1, 2)`
        RPC addRPC = new RPC("add", new Object[]{1, 2});
        Message<RPC> addRPCMessage = new Message<>("anything", addRPC);
        proxyMessageConsumer.accept(addRPCMessage);

        // after processing by the proxyMessageConsumer, we should have a reply containing the `#add(1, 2)` result
        Assert.assertEquals(replies.size(), 0);

    }

} 
