package machine.humanity.generating.ngram;

import machine.humanity.generating.TrainableGenerator;
import org.junit.Assert;
import org.junit.Test;

public class NGramSentenceGeneratorTest {

    /**
     * Tests that an NGramSentenceGenerator can be trained to generate a random sentence.
     */
    @Test
    public void testGenerate() throws Exception {
        TrainableGenerator sentenceGenerator = new NGramSentenceGenerator(1, 1, 1);
        sentenceGenerator.train("Hello world.");
        Assert.assertEquals("Hello world.", sentenceGenerator.generate());
    }

} 
