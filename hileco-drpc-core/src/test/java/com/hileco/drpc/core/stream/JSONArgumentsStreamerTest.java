package com.hileco.drpc.core.stream;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Philipp Gayret
 */
public class JSONArgumentsStreamerTest {

    /**
     * Serialize a bunch of primitives to bytes and back. The deserialized objects must match the input objects.
     */
    @Test
    public void testSerializeDeserializePrimitives() {

        JSONArgumentsStreamer JSONArgumentsStreamer = new JSONArgumentsStreamer();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Object[] arguments = {1, 2L, 3f};
        JSONArgumentsStreamer.serializeTo(out, arguments);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Object[] objects = JSONArgumentsStreamer.deserializeFrom(in, new Class[]{Integer.class, Long.class, Float.class});

        Assert.assertArrayEquals(arguments, objects);

    }

}
