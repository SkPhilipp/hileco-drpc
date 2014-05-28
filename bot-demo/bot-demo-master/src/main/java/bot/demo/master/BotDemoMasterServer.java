package bot.demo.master;

import bot.demo.master.api.RemoteMasterImpl;
import machine.humanity.api.services.GeneratorService;
import machine.lib.message.DelegatingMessageService;
import machine.lib.message.proxy.RemoteProxyBuilder;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
import machine.management.api.services.NetworkService;
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
        Config.set("HUMANITY_URL", "http://localhost:80/", configuration::setHumanityUrl);
        Config.set("HUMANITY_SOURCE", "v", configuration::setHumanitySource);
        Config.set("SERVER_PORT", 8080, configuration::setServerPort);
        BotDemoMasterServer server = new BotDemoMasterServer(configuration);
        server.start();
    }

    public void start() throws EmbeddedServerStartException {

        NetworkService networkService = JAXRSClientFactory.create(configuration.getManagementUrl(), NetworkService.class, PROVIDERS);
        DelegatingMessageService delegatingMessageService = new DelegatingMessageService(configuration.getServerPort(), networkService);
        RemoteProxyBuilder remoteProxyBuilder = new RemoteProxyBuilder(delegatingMessageService);

        String source = configuration.getHumanitySource();

        GeneratorService generatorService = JAXRSClientFactory.create(configuration.getHumanityUrl(), GeneratorService.class, PROVIDERS);
        generatorService.harvest(source);

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(delegatingMessageService);
        embeddedServer.start(services);

        RemoteMasterImpl remoteMaster = new RemoteMasterImpl(remoteProxyBuilder);
        remoteMaster.start();

    }

}
