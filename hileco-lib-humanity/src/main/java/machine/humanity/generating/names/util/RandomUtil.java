package machine.humanity.generating.names.util;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;

public class RandomUtil {

    public static int random(int limit) {
        return (int) Math.floor(Math.random() * ((double) limit));
    }

    public static Supplier<String> randomized(Collection<String> options) {
        String[] choices = options.toArray(new String[options.size()]);
        return () -> choices[random(choices.length)];
    }

    public static Supplier<String> cycled(Collection<String> options) {
        Iterator<String> iterator = Iterators.cycle(Lists.newArrayList(options));
        return iterator::next;
    }

}
