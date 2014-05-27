package bot.demo.master;

import bot.demo.messages.Topic;
import bot.demo.messages.process.ScanReply;
import machine.humanity.api.domain.HarvesterStatus;
import machine.humanity.api.services.GeneratorService;
import machine.lib.message.DelegatingMessageService;
import machine.lib.message.util.MessageHandlerBuilder;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
import machine.management.api.services.NetworkService;
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
        BotDemoMasterConfiguration configuration = new BotDemoMasterConfiguration();
        Config.set("MANAGEMENT_URL", "http://localhost:80/", configuration::setManagementUrl);
        Config.set("HUMANITY_URL", "http://localhost:80/", configuration::setHumanityUrl);
        Config.set("HUMANITY_SOURCE", "v", configuration::setHumanitySource);
        Config.set("SERVER_PORT", 8080, configuration::setServerPort);
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
        builder.onReceive(message -> {
            LOG.info("Received a message via built handler {}", message.getTopic());
            if (generatorService.status("g").equals(HarvesterStatus.HARVESTED)) {
                List<String> generated = generatorService.generate("g", 10);
                for (String entry : generated) {
                    LOG.info(entry);
                }
            }
        }).listen(Topic.PROCESS_SCAN_REPLY.toString());

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                builder.send(Topic.PROCESS_SCAN.toString());
            }
        }, 0, 2000);

    }

}
