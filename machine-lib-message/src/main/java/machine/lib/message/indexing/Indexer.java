package machine.lib.message.indexing;

import com.google.common.cache.Cache;
import machine.lib.message.HandlingMessageService;

import java.util.concurrent.TimeUnit;

public interface Indexer<K, V> {

    public Cache<K, V> getIndex();

    public void indexPassively(HandlingMessageService handlingMessageService);

    public void indexPassivelyAndScheduled(final HandlingMessageService handlingMessageService, int reindexPeriod, TimeUnit reindexTimeUnit);

}
