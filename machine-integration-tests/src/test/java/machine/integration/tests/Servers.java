package machine.integration.tests;

import bot.demo.consumer.BotDemoConsumerConfiguration;
import bot.demo.consumer.BotDemoConsumerServer;
import bot.demo.master.BotDemoMasterConfiguration;
import bot.demo.master.BotDemoMasterServer;
import machine.backbone.BackboneConfiguration;
import machine.backbone.BackboneServer;
import machine.humanity.HumanityConfiguration;
import machine.humanity.HumanityServer;
import machine.management.ManagementConfiguration;
import machine.management.ManagementServer;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * Starts up all management services locally, using bot-demo-* as samples.
 */
public class Servers {

    private static List<?> PROVIDERS = Collections.singletonList(new JacksonJsonProvider());

    public static final int MANAGEMENT_SERVER_PORT = 8000;
    public static final int BACKBONE_SERVER_PORT = 8100;
    public static final int HUMANITY_SERVER_PORT = 8200;
    public static final int BOT_DEMO_CONSUMER_SERVER_PORT = 8300;
    public static final int BOT_DEMO_MASTER_SERVER_PORT = 8400;

    @Test
    public void startAll() throws Exception {

        ManagementConfiguration managementConfiguration = new ManagementConfiguration();
        managementConfiguration.setServerPort(MANAGEMENT_SERVER_PORT);
        ManagementServer managementServer = new ManagementServer(managementConfiguration);
        managementServer.start();

        BackboneConfiguration backboneConfiguration = new BackboneConfiguration();
        backboneConfiguration.setConfigurationDir("/etc/backbone-test-1");
        backboneConfiguration.setDefaultHeartbeatPeriod(5000);
        backboneConfiguration.setDefaultServerPort(BACKBONE_SERVER_PORT);
        backboneConfiguration.setDefaultManagementUrl(String.format("http://localhost:%d", managementConfiguration.getServerPort()));
        BackboneServer backboneServer = new BackboneServer(backboneConfiguration);
        backboneServer.start();

        HumanityConfiguration humanityConfiguration = new HumanityConfiguration();
        humanityConfiguration.setServerPort(HUMANITY_SERVER_PORT);
        HumanityServer humanityServer = new HumanityServer(humanityConfiguration);
        humanityServer.start();

        BotDemoConsumerConfiguration botDemoConsumerConfiguration = new BotDemoConsumerConfiguration();
        botDemoConsumerConfiguration.setServerPort(BOT_DEMO_CONSUMER_SERVER_PORT);
        botDemoConsumerConfiguration.setBackboneUrl(String.format("http://localhost:%d", backboneConfiguration.getDefaultServerPort()));
        BotDemoConsumerServer botDemoConsumerServer = new BotDemoConsumerServer(botDemoConsumerConfiguration);
        botDemoConsumerServer.start();

        BotDemoMasterConfiguration botDemoMasterConfiguration = new BotDemoMasterConfiguration();
        botDemoMasterConfiguration.setServerPort(BOT_DEMO_MASTER_SERVER_PORT);
        botDemoMasterConfiguration.setManagementUrl(String.format("http://localhost:%d", managementConfiguration.getServerPort()));
        BotDemoMasterServer botDemoMasterServer = new BotDemoMasterServer(botDemoMasterConfiguration);
        botDemoMasterServer.start();

        Thread.sleep(600000);

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
