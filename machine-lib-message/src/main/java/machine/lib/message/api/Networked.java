package machine.lib.message.api;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Networked<T extends Invokeable> {

    /**
     * @return a dynamically generated implementation of {@link T}
     */
    public T getImplementation();

    // TODO: document
    public <R> AutoCloseable callDistributed(Function<T, R> invoker, Consumer<R> consumer);

}
