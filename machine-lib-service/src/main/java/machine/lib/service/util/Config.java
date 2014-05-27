package machine.lib.service.util;

import com.google.common.primitives.Ints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static void set(String propertyName, String defaultValue, Consumer<String> consumer) {
        String value = System.getProperty(propertyName, defaultValue);
        LOG.info("{}: {}", propertyName, value);
        consumer.accept(value);
    }

    public static void set(String propertyName, Integer defaultValue, Consumer<Integer> consumer) {
        Integer value = Ints.tryParse(System.getProperty(propertyName, Integer.toString(defaultValue)));
        LOG.info("{}: {}", propertyName, value);
        consumer.accept(value);
    }

}
