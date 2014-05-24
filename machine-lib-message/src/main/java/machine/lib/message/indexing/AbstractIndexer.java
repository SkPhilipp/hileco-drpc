package machine.lib.message.indexing;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import machine.lib.message.DelegatingMessageService;
import machine.lib.message.MessageHandler;
import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An abstract "Indexer", passively and actively maintains a <K, V> cache called the "index", by listening for index
 * responses and publishing index requests on a {@link machine.lib.message.DelegatingMessageService}.
 *
 * @param <REST> index-response message type
 * @param <REQT> index-requerst message type
 * @param <K> index key type
 * @param <V> index value type
 */
abstract public class AbstractIndexer<REST extends Serializable, REQT extends Serializable, K, V> extends MessageHandler<REST> implements Indexer<K, V> {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(0);
    private String passiveIndexTopic;
    private String activeIndexTopic;
    private Cache<K, V> index;

    /**
     * - Instantiates an index using the given expire parameters.
     *
     * @param passiveIndexTopic the topic under which to listen for index responses
     * @param activeIndexTopic the topic under which to send index requests
     * @param expireDuration the expire duration after write of the index
     * @param expireTimeUnit expireDuration's TimeUnit
     */
    public AbstractIndexer(String passiveIndexTopic, String activeIndexTopic, int expireDuration, TimeUnit expireTimeUnit) {
        this.passiveIndexTopic = passiveIndexTopic;
        this.activeIndexTopic = activeIndexTopic;
        this.index = CacheBuilder.newBuilder()
                .expireAfterWrite(expireDuration, expireTimeUnit)
                .build();
    }

    /**
     * Registers to passively reindex.
     *
     * @param delegatingMessageService the service which to use for publishing and listening on
     */
    public void indexPassively(DelegatingMessageService delegatingMessageService){
        delegatingMessageService.registerHandler(passiveIndexTopic, this);
    }

    /**
     * Registers to passively reindex, and scheduled to actively reindex.
     *
     * @param delegatingMessageService the service which to use for publishing and listening on
     * @param reindexPeriod the duration at which to schedule index requests
     * @param reindexTimeUnit reindexPeriod's TimeUnit
     */
    public void indexPassivelyAndScheduled(final DelegatingMessageService delegatingMessageService, int reindexPeriod, TimeUnit reindexTimeUnit){
        this.indexPassively(delegatingMessageService);
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                AbstractIndexer self = AbstractIndexer.this;
                delegatingMessageService.publish(self.activeIndexTopic, self.buildIndexRequest());
            }
        }, 0, reindexPeriod, reindexTimeUnit);
    }

    abstract public REQT buildIndexRequest();

    @Override
    abstract public void handle(NetworkMessage<?> message);

    public Cache<K, V> getIndex() {
        return index;
    }

}
