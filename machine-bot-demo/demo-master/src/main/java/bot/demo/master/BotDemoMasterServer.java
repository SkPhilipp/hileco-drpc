package bot.demo.master;

import bot.demo.master.api.MasterServiceImpl;
import com.google.common.primitives.Ints;
import machine.lib.message.DelegatingMessageService;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
import machine.drcp.core.routing.services.RouterService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BotDemoMasterServer implements LocalServer {

    private static final List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());
    private final BotDemoMasterConfiguration configuration;

    public BotDemoMasterServer(BotDemoMasterConfiguration configuration) throws EmbeddedServerStartException {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws Exception {
        BotDemoMasterConfiguration configuration = new BotDemoMasterConfiguration();
        Config.set("MANAGEMENT_URL", "http://localhost:80/", configuration::setManagementUrl);
        Config.set("HUMANITY_SOURCE", "v", configuration::setHumanitySource);
        Config.set("SERVER_PORT", 8080, configuration::setServerPort, Ints::tryParse);
        BotDemoMasterServer server = new BotDemoMasterServer(configuration);
        server.start();
    }

    public void start() throws EmbeddedServerStartException {

        RouterService routerService = JAXRSClientFactory.create(configuration.getManagementUrl(), RouterService.class, PROVIDERS);
        DelegatingMessageService delegatingMessageService = new DelegatingMessageService(configuration.getServerPort(), routerService);

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(delegatingMessageService);
        embeddedServer.start(services);

        MasterServiceImpl remoteMaster = new MasterServiceImpl(delegatingMessageService);
        remoteMaster.start();

    }

}