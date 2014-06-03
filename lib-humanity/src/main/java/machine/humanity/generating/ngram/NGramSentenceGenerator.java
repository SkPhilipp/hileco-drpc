package machine.humanity.generating.ngram;

import com.google.common.base.Joiner;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * N-gram based generator, counts word combinations, training this maps words to words that follow words; When trained
 * with sample text: "ngram trainable generator", with `n = 1`, functionally it internally maps:
 * <p/>
 * - "ngram" can be used to begin a sentence.
 * - "trainable" can be used after "ngram".
 * - "generator" can be used after "trainable".
 * - "trainable" can be used to end a sentence.
 * <p/>
 * With `n = 2` and the same sentence, it would map:
 * <p/>
 * - "ngram trainable" can be used to begin a sentence.
 * - "generator" can be used after "ngram trainable".
 * - "trainable generator" can be used to end a sentence.
 * <p/>
 * Meaning the higher `n`; the more complex the mapping, the more memory used, and the smarter the sentances will
 * appear. However; using high `n` will also require much more data to be mapped in order to increase the diversity
 * of the sentences generated, meaning even more memory use if chosen.
 * <p/>
 * When generating the generator randomly chooses words based on how many times they appear as followup words, meaning
 * when trained with:
 * - "hello ngram", once
 * - "hello world", 5 times
 * <p/>
 * Then there is a five in six chance the generator choses "hello world"
 */
public class NGramSentenceGenerator implements Consumer<String>, Supplier<String> {

    /**
     * A filter which returns true when the key is an {@link NGram#END}.
     */
    public static final BiPredicate<String, String> ONLY_ENDS = (key, value) -> NGram.END.equals(key);

    public static final Pattern LINE_ENDS = Pattern.compile("\\s+\\.");
    private final NGram starts;
    private final NGram follows;
    private Integer attemptMinimumLength;
    private Integer attemptMaximumIterations;

    /**
     * @param n amount of consecutive word combinations to map
     */
    public NGramSentenceGenerator(Integer n) {
        this(n, 2, 5);
    }

    /**
     * @param n                        amount of consecutive word combinations to map
     * @param attemptMaximumIterations maximum tries to generate a random sentence of given min length
     * @param attemptMinimumLength     minimum sentence length in words
     */
    public NGramSentenceGenerator(Integer n, Integer attemptMaximumIterations, Integer attemptMinimumLength) {
        this.starts = new NGram(1, n, ONLY_ENDS);
        this.follows = new NGram(n, 1, (key, value) -> true);
        this.attemptMaximumIterations = attemptMaximumIterations;
        this.attemptMinimumLength = attemptMinimumLength;
    }

    @Override
    public void accept(String input) {
        this.starts.train(input);
        this.follows.train(input);
    }

    /**
     * @return generated string
     */

    @Override
    public String get() {
        for (int i = 0; true; i++) {
            List<String> begin = this.starts.randomArray(NGram.END);
            List<String> result = this.follows.sentence(begin);
            if (result.size() > attemptMinimumLength || i == attemptMaximumIterations) {
                CharSequence joined = Joiner.on(' ').join(result);
                return LINE_ENDS.matcher(joined).replaceAll(".");
            }
        }
    }

}
