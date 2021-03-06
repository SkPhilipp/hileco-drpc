package com.hileco.drpc.http;

import com.google.common.collect.Lists;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.core.HttpHeaderUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philipp Gayret
 */
public class HttpHeaderUtilsTest {

    @Test
    public void testCallbackSerializeDeserialize() {

        Map<String, String> headers = new HashMap<>();

        Metadata metadata = new Metadata("123", "654");

        HttpHeaderUtils.writeHeaders(metadata, headers::put);
        Metadata converted = HttpHeaderUtils.fromHeaders(headers::get);

        Assert.assertEquals(metadata.getType(), converted.getType());
        Assert.assertEquals(metadata.getId(), converted.getId());
        Assert.assertEquals(metadata.getReplyTo(), converted.getReplyTo());

    }

    @Test
    public void testCallSerializeDeserialize() {

        Map<String, String> headers = new HashMap<>();

        Metadata metadata = new Metadata("123", "aService", "doOperation", Lists.newArrayList("a", "b", "c"));

        HttpHeaderUtils.writeHeaders(metadata, headers::put);
        Metadata converted = HttpHeaderUtils.fromHeaders(headers::get);

        Assert.assertEquals(metadata.getType(), converted.getType());
        Assert.assertEquals(metadata.getId(), converted.getId());
        Assert.assertEquals(metadata.getService(), converted.getService());
        Assert.assertEquals(metadata.getOperation(), converted.getOperation());
        Assert.assertEquals(metadata.getTargets(), converted.getTargets());

    }

}
