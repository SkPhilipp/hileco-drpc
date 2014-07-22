package com.hileco.drcp.core.client;

import com.hileco.drcp.core.api.MessageClient;
import com.hileco.drcp.core.api.models.Message;
import com.hileco.drcp.core.api.models.RPC;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProxyMessageConsumerTest {

    public static interface SampleService {

        public Integer add(Integer a, Integer b);

    }

    /**
     * Performs an actual RPC Message invocation on a ProxyMessageConsumer using a test message client
     * and a test service. This test verifies that the consumer can successfully convert an RPC Message
     * to an actual Java call on the service, and properly returns a response through the given MessageClient.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRPC() throws Exception {

        // a list to store expected rpc-replies made on the message client
        Map<UUID, Message<?>> replies = new HashMap<>();

        // a message client which stores the message and returns a randomized key when called
        MessageClient messageClient = (message) -> {
            UUID messageId = UUID.randomUUID();
            replies.put(messageId, message);
            return messageId;
        };

        // a sample service for the proxy message consumer to call
        SampleService sampleService = (a, b) -> a + b;

        // an actual ProxyMessageConsumer to so send an RPC Message to which should invoke the sample service
        ProxyMessageConsumer proxyMessageConsumer = new ProxyMessageConsumer(messageClient, sampleService, ObjectConverter.RAW);

        // at this point we have everything set up to perform actual RPC Message calls, we'll be invoking the service as `#add(1, 2)`
        RPC addRPC = new RPC("add", new Object[]{1, 2});
        Message<RPC> addRPCMessage = new Message<>("anything", addRPC);
        proxyMessageConsumer.accept(addRPCMessage);

        // we've made the call, the proxy message consumer is done, meaning:
        // - it should've converted the RPC Message to an actual invocation of the service
        // - the service should've been called and returned a result
        // - the result should've been converted to a result message
        // - the result message shouldve been published on the message client
        // - our test message client stores calls in the replies list, meaing it should have 1 reply-entry
        Assert.assertEquals(replies.size(), 1);

        // to be completely sure the reply content is the result of the sample call we do another assertion
        Message<Integer> reply = (Message<Integer>) replies.values().iterator().next();
        Assert.assertEquals(reply.getContent(), sampleService.add(1, 2));

    }

} 
