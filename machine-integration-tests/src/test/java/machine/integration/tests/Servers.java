package machine.integration.tests;

import bot.demo.consumer.BotDemoConsumerConfiguration;
import bot.demo.consumer.BotDemoConsumerServer;
import bot.demo.master.BotDemoMasterConfiguration;
import bot.demo.master.BotDemoMasterServer;
import machine.backbone.BackboneConfiguration;
import machine.backbone.BackboneServer;
import machine.humanity.HumanityConfiguration;
import machine.humanity.HumanityServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.management.ManagementConfiguration;
import machine.management.ManagementServer;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Starts up all management services locally, using bot-demo-* as samples.
 */
public class Servers {

    public static final int MANAGEMENT_SERVER_PORT = 8000;
    public static final int BACKBONE_SERVER_PORT = 8100;
    public static final int HUMANITY_SERVER_PORT = 8200;
    public static final int BOT_DEMO_CONSUMER_SERVER_PORT = 8300;
    public static final int BOT_DEMO_MASTER_SERVER_PORT = 8400;
    private static final Logger LOG = LoggerFactory.getLogger(Servers.class);
    private static List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());

    public void start(LocalServer localServer) throws Exception {
        Thread thread = new Thread(() -> {
            try {
                localServer.start();
            } catch (EmbeddedServerStartException e) {
                throw new IllegalStateException(e);
            }
        });
        thread.start();
        thread.join();
    }

    @Test
    public void startAll() throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            LOG.error("Thread erred", exception);
        });

        ManagementConfiguration managementConfiguration = new ManagementConfiguration();
        managementConfiguration.setServerPort(MANAGEMENT_SERVER_PORT);
        ManagementServer managementServer = new ManagementServer(managementConfiguration);

        BackboneConfiguration backboneConfiguration = new BackboneConfiguration();
        backboneConfiguration.setConfigurationDir("/etc/backbone-test-1");
        backboneConfiguration.setDefaultHeartbeatPeriod(5000);
        backboneConfiguration.setDefaultServerPort(BACKBONE_SERVER_PORT);
        backboneConfiguration.setDefaultManagementUrl(String.format("http://localhost:%d", managementConfiguration.getServerPort()));
        BackboneServer backboneServer = new BackboneServer(backboneConfiguration);

        HumanityConfiguration humanityConfiguration = new HumanityConfiguration();
        humanityConfiguration.setServerPort(HUMANITY_SERVER_PORT);
        HumanityServer humanityServer = new HumanityServer(humanityConfiguration);

        BotDemoConsumerConfiguration botDemoConsumerConfiguration = new BotDemoConsumerConfiguration();
        botDemoConsumerConfiguration.setServerPort(BOT_DEMO_CONSUMER_SERVER_PORT);
        botDemoConsumerConfiguration.setBackboneUrl(String.format("http://localhost:%d", backboneConfiguration.getDefaultServerPort()));
        BotDemoConsumerServer botDemoConsumerServer = new BotDemoConsumerServer(botDemoConsumerConfiguration);

        BotDemoMasterConfiguration botDemoMasterConfiguration = new BotDemoMasterConfiguration();
        botDemoMasterConfiguration.setServerPort(BOT_DEMO_MASTER_SERVER_PORT);
        botDemoMasterConfiguration.setManagementUrl(String.format("http://localhost:%d", managementConfiguration.getServerPort()));
        BotDemoMasterServer botDemoMasterServer = new BotDemoMasterServer(botDemoMasterConfiguration);

        start(managementServer);
        start(backboneServer);
        start(humanityServer);
        start(botDemoConsumerServer);
        start(botDemoMasterServer);

        Thread.sleep(60000);

    }

    public <T> T getService(Class<?> module, Class<T> service) {
        Integer port;
        if (ManagementServer.class.equals(module)) {
            port = MANAGEMENT_SERVER_PORT;
        } else if (HumanityServer.class.equals(module)) {
            port = HUMANITY_SERVER_PORT;
        } else if (BackboneServer.class.equals(module)) {
            port = BACKBONE_SERVER_PORT;
        } else if (BotDemoConsumerServer.class.equals(module)) {
            port = BOT_DEMO_CONSUMER_SERVER_PORT;
        } else if (BotDemoMasterServer.class.equals(module)) {
            port = BOT_DEMO_MASTER_SERVER_PORT;
        } else {
            throw new Error("That's not a module: " + module);
        }
        String url = String.format("http://localhost:%d/", port);
        return JAXRSClientFactory.create(url, service, PROVIDERS);

    }

}
