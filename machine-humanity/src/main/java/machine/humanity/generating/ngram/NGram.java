package machine.humanity.generating.ngram;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class NGram {

    public static final String SPLIT = " ";
    public static final String END = ".";
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern ENDS = Pattern.compile("([!?\\.]+\\s*)+");
    private static final Pattern QUOTE_BEGIN = Pattern.compile("^[\"'`]");
    private static final Pattern QUOTE_END = Pattern.compile("[\"'`]$");
    private static final Pattern COMMA = Pattern.compile(",");
    private Integer nKey;
    private Integer nValue;
    private Map<String, WordCounter> internal;
    private final EntryFilter filter;

    /**
     * @param nKey   key size, at least 1
     * @param nValue value size, at least 1
     */
    public NGram(Integer nKey, Integer nValue, EntryFilter filter) {
        this.filter = filter;
        this.nKey = nKey;
        this.nValue = nValue;
        this.internal = new HashMap<String, WordCounter>();
    }

    /**
     * Normalizes a piece of text, splits words and replaces line-ending characters with `ngram.end` entries.
     *
     * @param raw raw piece of text
     * @return normalized text split into words
     */
    public static List<String> clean(String raw) {

        // split the given input into an array of words after removing all quoteish characters.
        String commaless = COMMA.matcher(raw).replaceAll(" ");
        String beginQuoteless = QUOTE_BEGIN.matcher(commaless).replaceAll(" ");
        String quoteless = QUOTE_END.matcher(beginQuoteless).replaceAll(" ");
        String unended = ENDS.matcher(quoteless).replaceAll(". ");
        List<String> splitted = Splitter.on(WHITESPACE).splitToList(unended);
        List<String> result = Lists.newArrayList(END);

        for (String item : splitted) {
            result.add(ENDS.matcher(item).replaceAll(""));
            if (ENDS.matcher(item).find() && !END.equals(result.get(result.size() - 1))) {
                result.add(END);
            }
        }

        if (!END.equals(result.get(result.size() - 1))) {
            result.add(END);
        }

        return result;
    }

    /**
     * Trains usign a given piece ot text.
     *
     * @param raw the raw text to first be cleaned and then used for training
     */
    public void train(String raw) {
        List<String> words = clean(raw);
        for (int i = 0; i < (words.size() - this.nKey) - (this.nValue - 1); i++) {
            String key = this.key(words.subList(i, i + this.nKey));
            String value = Joiner.on(SPLIT).join(words.subList(i + this.nKey, i + this.nKey + this.nValue));
            if (this.filter.allow(key, value)) {
                this.get(key).count(value);
            }
        }
    }

    /**
     * Returns the map of word counts for the given key.
     *
     * @param key the key into the internal map
     * @return WordCounter, never null
     */
    private WordCounter get(String key) {
        if (!this.internal.containsKey(key)) {
            this.internal.put(key, new WordCounter());
        }
        return this.internal.get(key);
    }

    /**
     * Constructs a key for use in the ngram.
     *
     * @param words words to convert to a key
     * @return the key
     */
    public String key(Iterable<String> words) {
        return Joiner.on(SPLIT).join(words);
    }

    /**
     * Returns a random entry for a given key, returns undefined if the given key is unmapped.
     *
     * @param key key for which to find a map for, use {@link #END} to get sentance beginnings
     * @return a random word
     */
    public String random(String key) {
        WordCounter map = this.get(key);
        Integer randomOffset = (int) (Math.random() * map.getTotal());
        String chosen = map.getAt(randomOffset);
        return chosen != null ? chosen : END;
    }

    /**
     * Wraps ngram.prototype.rand, splitting the result into an array.
     *
     * @param key key for use with {@link #random(String)}, use {@link #END} to get sentance beginnings
     * @return list of words
     */
    public List<String> randomArray(String key) {
        return Splitter.on(SPLIT).splitToList(this.random(key));
    }

    /**
     * Generates a random sentence.
     *
     * @param words to construct initial key with, only last `nKey` are used
     * @return list of randomly chosen words
     */
    public List<String> sentence(List<String> words) {
        List<String> result = Lists.newArrayList(words);
        Integer last = Math.max(words.size() - this.nKey, 0);
        List<String> keywords = Lists.newArrayList(words.subList(last, last + this.nKey));
        String key = this.key(keywords);
        List<String> value;
        do {
            value = this.randomArray(key);
            result.addAll(value);
            if (keywords.size() == this.nKey) {
                keywords = Lists.newArrayList(keywords.subList(0, this.nKey - 1));
            }
            keywords.addAll(value);
            key = this.key(keywords);
        }
        while (!END.equals(value.get(value.size() - 1)));
        return result;
    }

}