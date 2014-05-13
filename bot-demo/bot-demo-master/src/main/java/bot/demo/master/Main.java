package bot.demo.master;

import bot.demo.messages.ScanReply;
import bot.demo.messages.Topics;
import com.google.common.cache.Cache;
import com.google.common.primitives.Ints;
import machine.lib.message.HandlingMessageService;
import machine.lib.message.indexing.Indexer;
import machine.lib.message.indexing.RequestlessAbstractIndexer;
import machine.lib.service.EmbeddedServer;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String MANAGEMENT_URL = System.getProperty("MANAGEMENT_URL", "http://localhost:80/");
    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "8080"));
    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        // log environment
        LOG.info("Starting with:");
        LOG.info("- MANAGEMENT_URL: {}", MANAGEMENT_URL);
        LOG.info("- SERVER_PORT: {}", SERVER_PORT);
        // set up services
        NetworkService networkService = JAXRSClientFactory.create(MANAGEMENT_URL, NetworkService.class, PROVIDERS);
        HandlingMessageService handlingMessageService = new HandlingMessageService(SERVER_PORT, networkService);
        // run the server
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(handlingMessageService);
        embeddedServer.start(services);
        // do a sample callback

        Indexer<UUID, Long> indexer = new RequestlessAbstractIndexer<ScanReply, UUID, Long>(Topics.SCAN_REPLY, Topics.SCAN, 2, TimeUnit.MINUTES) {
            @Override
            public void handle(NetworkMessage<?> message) {
                Cache<UUID, Long> index = this.getIndex();
                ScanReply scanReply = this.open(message);
                UUID serverId = scanReply.getServerId();
                long value = System.currentTimeMillis();
                index.put(serverId, value);
                LOG.info("bot-demo-consumer {} last seen {}", serverId, value);
            }
        };

        indexer.indexPassivelyAndScheduled(handlingMessageService, 5, TimeUnit.SECONDS);

    }

}
