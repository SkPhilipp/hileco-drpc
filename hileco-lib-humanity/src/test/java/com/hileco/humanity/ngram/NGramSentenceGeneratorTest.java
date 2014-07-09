package com.hileco.humanity.ngram;

import org.junit.Assert;
import org.junit.Test;

public class NGramSentenceGeneratorTest {

    /**
     * Tests that an NGramSentenceGenerator can be trained to generate a random sentence.
     */
    @Test
    public void testGenerate() throws Exception {
        NGramSentenceGenerator sentenceGenerator = new NGramSentenceGenerator(1, 1, 1);
        sentenceGenerator.accept("Hello world.");
        Assert.assertEquals("Hello world.", sentenceGenerator.get());
    }

} 
