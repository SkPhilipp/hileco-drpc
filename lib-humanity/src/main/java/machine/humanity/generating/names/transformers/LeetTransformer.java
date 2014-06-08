package machine.humanity.generating.names.transformers;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import machine.humanity.generating.names.util.RandomUtil;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Transforms a string into a much more 1337 string.
 */
public class LeetTransformer implements Function<String, String> {

    private static Multimap<Character, String> SUBSTITUTIONS = HashMultimap.create();

    private Multimap<Character, String> substitutions;
    private int chance;

    static {
        SUBSTITUTIONS.put('a', "@");
        SUBSTITUTIONS.put('a', "4");
        SUBSTITUTIONS.put('e', "3");
        SUBSTITUTIONS.put('l', "!");
        SUBSTITUTIONS.put('l', "1");
        SUBSTITUTIONS.put('s', "5");
        SUBSTITUTIONS.put('s', "$");
        SUBSTITUTIONS.put('o', "0");
        SUBSTITUTIONS.put('t', "7");
    }

    /**
     * @param predicate to check which characters are allowed
     * @param chance    chance per replaceable character to replace it
     */
    public LeetTransformer(Predicate<String> predicate, int chance) {
        Preconditions.checkArgument(chance <= 100 && chance >= 0, "Chance must be equal to or between 0 and 100");
        this.chance = chance;
        this.substitutions = HashMultimap.create();
        Stream<Map.Entry<Character, String>> filtered = SUBSTITUTIONS.entries().stream().filter(entry -> predicate.test(entry.getValue()));
        filtered.forEach(entry -> this.substitutions.put(entry.getKey(), entry.getValue()));
    }

    @Override
    public String apply(String input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Character element : input.toCharArray()) {
            if (substitutions.containsKey(element) && chance >= RandomUtil.random(100)) {
                String choice = RandomUtil.cycled(substitutions.get(element)).get();
                stringBuilder.append(choice);
            } else {
                stringBuilder.append(element);
            }
        }
        return stringBuilder.toString();
    }

}
