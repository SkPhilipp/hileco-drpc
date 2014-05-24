package bot.demo.master;

import bot.demo.messages.ScanReply;
import bot.demo.messages.Topics;
import com.google.common.cache.Cache;
import com.google.common.primitives.Ints;
import machine.lib.message.DelegatingMessageService;
import machine.lib.message.indexing.Indexer;
import machine.lib.message.indexing.RequestlessAbstractIndexer;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BotDemoMasterServer {

    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());
    private static final Logger LOG = LoggerFactory.getLogger(BotDemoMasterServer.class);
    private final BotDemoMasterConfiguration configuration;

    public BotDemoMasterServer(BotDemoMasterConfiguration configuration) throws EmbeddedServerStartException {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws Exception {

        String MANAGEMENT_URL = System.getProperty("MANAGEMENT_URL", "http://localhost:80/");
        Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "8080"));

        LOG.info("MANAGEMENT_URL: {}", MANAGEMENT_URL);
        LOG.info("SERVER_PORT: {}", SERVER_PORT);

        BotDemoMasterConfiguration configuration = new BotDemoMasterConfiguration();
        configuration.setManagementUrl(MANAGEMENT_URL);
        configuration.setServerPort(SERVER_PORT);

        BotDemoMasterServer server = new BotDemoMasterServer(configuration);
        server.start();

    }

    public void start() throws EmbeddedServerStartException {

        NetworkService networkService = JAXRSClientFactory.create(configuration.getManagementUrl(), NetworkService.class, PROVIDERS);
        DelegatingMessageService delegatingMessageService = new DelegatingMessageService(configuration.getServerPort(), networkService);

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(delegatingMessageService);
        embeddedServer.start(services);

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
        indexer.indexPassivelyAndScheduled(delegatingMessageService, 5, TimeUnit.SECONDS);

    }

}
