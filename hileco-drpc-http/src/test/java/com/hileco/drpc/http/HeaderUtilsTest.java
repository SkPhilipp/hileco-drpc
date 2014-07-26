package com.hileco.drpc.http;

import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.servlet.IncomingMessageConsumerServletAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HeaderUtilsTest {

    @Test
    public void testCallbackSerializeDeserialize() {

        Map<String, String> headers = new HashMap<>();

        Metadata metadata = new Metadata("123", "654");

        IncomingMessageConsumerServletAdapter.HeaderUtils headerUtils = new IncomingMessageConsumerServletAdapter.HeaderUtils();
        headerUtils.writeHeaders(metadata, headers::put);
        Metadata converted = headerUtils.fromHeaders(headers::get);

        Assert.assertEquals(metadata.getType(), converted.getType());
        Assert.assertEquals(metadata.getId(), converted.getId());
        Assert.assertEquals(metadata.getReplyTo(), converted.getReplyTo());

    }

    @Test
    public void testCallSerializeDeserialize() {

        Map<String, String> headers = new HashMap<>();

        Metadata metadata = new Metadata("123", "aService", "doOperation");

        IncomingMessageConsumerServletAdapter.HeaderUtils headerUtils = new IncomingMessageConsumerServletAdapter.HeaderUtils();
        headerUtils.writeHeaders(metadata, headers::put);
        Metadata converted = headerUtils.fromHeaders(headers::get);

        Assert.assertEquals(metadata.getType(), converted.getType());
        Assert.assertEquals(metadata.getId(), converted.getId());
        Assert.assertEquals(metadata.getService(), converted.getService());
        Assert.assertEquals(metadata.getOperation(), converted.getOperation());

    }

}
