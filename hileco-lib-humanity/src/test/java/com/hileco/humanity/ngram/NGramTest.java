package com.hileco.humanity.ngram;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

public class NGramTest {

    /**
     * Tests that an NGram can be trained to generate a random word.
     */
    @Test
    public void testTrainRandom() throws Exception {
        NGram nGram = new NGram(1, 1, (key, value) -> true);
        nGram.train("Hello world");
        Assert.assertEquals("world", nGram.random("Hello"));
    }

    /**
     * Tests that an NGram can be trained to generate a random sentence.
     */
    @Test
    public void testTrainSentence() throws Exception {
        NGram nGram = new NGram(1, 1, (key, value) -> true);
        nGram.train("Hello world");
        Assert.assertEquals(Lists.newArrayList(NGram.END, "Hello", "world", NGram.END), nGram.sentence(Lists.newArrayList(NGram.END)));
    }

}
