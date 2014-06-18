package machine.lib.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static void set(String propertyName, String defaultValue, Consumer<String> consumer) {
        String value = System.getProperty(propertyName, defaultValue);
        LOG.info("{}: {}", propertyName, value);
        consumer.accept(value);
    }

    public static <T> void set(String propertyName, T defaultValue, Consumer<T> consumer, Function<String, T> parser) {
        String propertyValue = System.getProperty(propertyName);
        T value = propertyValue == null ? defaultValue : parser.apply(propertyValue);
        LOG.info("{}: {}", propertyName, value);
        consumer.accept(value);
    }

}
