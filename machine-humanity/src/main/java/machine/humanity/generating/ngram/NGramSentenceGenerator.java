package machine.humanity.generating.ngram;

import com.google.common.base.Joiner;

import java.util.List;

public class NGramSentenceGenerator {

    /**
     * A filter which returns true when the key is an {@link NGram#END}.
     */
    public static final EntryFilter ONLY_ENDS = new EntryFilter() {

        @Override
        public boolean allow(String key, String value) {
            return NGram.END.equals(key);
        }

    };

    private final NGram starts;
    private final NGram follows;

    public NGramSentenceGenerator(Integer n){
        this.starts = new NGram(1, n, ONLY_ENDS);
        this.follows = new NGram(n, 1, EntryFilter.ALLOW_ALL);
    }

    public void train(String input){
        this.starts.train(input);
        this.follows.train(input);
    }

    /**
     * @param min minimum sentence length in words
     * @param iterMax maximum tries to generate a random sentence of given min length
     * @return generated string
     */
    public String generate(Integer min, Integer iterMax){
        for(int i = 0; true; i++){
            List<String> begin = this.starts.randomArray(NGram.END);
            List<String> result = this.follows.sentence(begin);
            if(result.size() > min || i == iterMax){
                return Joiner.on(' ').join(result); /// TODO(?): .replace(/\s+\./g, '.');
            }
        }
    }

}
