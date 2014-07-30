package com.hileco.drpc.core.json;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Philipp Gayret
 */
public class JSONArgumentsStreamerTest {

    /**
     * Serialize a bunch of primitives to bytes and back. The deserialized objects must match the input objects.
     */
    @Test
    public void testSerializeDeserializePrimitives() throws IOException {

        JSONArgumentsStreamer JSONArgumentsStreamer = new JSONArgumentsStreamer();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Object[] arguments = {1, 2L, 3f};
        JSONArgumentsStreamer.serializeTo(out, arguments);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Object[] objects = JSONArgumentsStreamer.deserializeFrom(in, new Class[]{Integer.class, Long.class, Float.class});

        Assert.assertArrayEquals(arguments, objects);

    }

}
