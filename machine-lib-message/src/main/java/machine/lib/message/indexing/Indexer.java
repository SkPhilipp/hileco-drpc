package machine.lib.message.indexing;

import com.google.common.cache.Cache;
import machine.lib.message.DelegatingMessageService;

import java.util.concurrent.TimeUnit;

public interface Indexer<K, V> {

    public Cache<K, V> getIndex();

    public void indexPassively(DelegatingMessageService delegatingMessageService);

    public void indexPassivelyAndScheduled(final DelegatingMessageService delegatingMessageService, int reindexPeriod, TimeUnit reindexTimeUnit);

}
