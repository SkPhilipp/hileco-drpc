package machine.lib.message.indexing;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link AbstractIndexer}, where all index request messages are empty.
 */
abstract public class RequestlessAbstractIndexer <REQT extends Serializable, K, V> extends AbstractIndexer<REQT, Serializable, K, V> {


    /**
     * - Instantiates an index using the given expire parameters.
     *
     * @param passiveIndexTopic the topic under which to listen for index responses
     * @param activeIndexTopic  the topic under which to send index requests
     * @param expireDuration    the expire duration after write of the index
     * @param expireTimeUnit    expireDuration's TimeUnit
     */
    public RequestlessAbstractIndexer(String passiveIndexTopic, String activeIndexTopic, int expireDuration, TimeUnit expireTimeUnit) {
        super(passiveIndexTopic, activeIndexTopic, expireDuration, expireTimeUnit);
    }

    @Override
    public Serializable buildIndexRequest() {
        return null;
    }

}
