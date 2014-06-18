package machine.humanity.generating.names.transformers;

import org.junit.Assert;
import org.junit.Test;

public class LeetTransformerTest {

    @Test
    public void testApply() throws Exception {
        LeetTransformer leetTransformer = new LeetTransformer(s -> s.matches("\\d"), 100);
        Assert.assertEquals("1337 7r4n5f0rm3r", leetTransformer.apply("leet transformer"));
    }

}