package bot.demo.master;

import bot.demo.messages.ScanReply;
import bot.demo.messages.Topics;
import com.google.common.primitives.Ints;
import machine.humanity.api.domain.HarvesterStatus;
import machine.humanity.api.services.GeneratorService;
import machine.lib.message.DelegatingMessageService;
import machine.lib.message.util.MessageHandlerBuilder;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.management.api.services.NetworkService;
import machine.message.api.entities.NetworkMessage;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BotDemoMasterServer implements LocalServer {

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

        GeneratorService generatorService = JAXRSClientFactory.create(configuration.getHumanityUrl(), GeneratorService.class, PROVIDERS);
        generatorService.harvest("g");

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(delegatingMessageService);
        embeddedServer.start(services);

        MessageHandlerBuilder<ScanReply> builder = new MessageHandlerBuilder<>(ScanReply.class, delegatingMessageService);
        builder.onReceive((NetworkMessage<?> networkMessage, ScanReply content) -> {
            LOG.info("Received a message via built handler {}", networkMessage.getTopic());
            //builder.send(Topics.SCAN);
            if (generatorService.status("g").equals(HarvesterStatus.HARVESTED)) {
                List<String> generated = generatorService.generate("g", 10);
                for (String entry : generated) {
                    LOG.info(entry);
                }
                LOG.info("Received a message via built handler {}", networkMessage.getTopic());
            }
        }).listen(Topics.SCAN_REPLY);

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                builder.send(Topics.SCAN);
            }
        }, 0, 2000);

    }

}
