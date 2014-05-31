package machine.integration.tests;

import bot.demo.consumer.BotDemoConsumerConfiguration;
import bot.demo.consumer.BotDemoConsumerServer;
import bot.demo.master.BotDemoMasterConfiguration;
import bot.demo.master.BotDemoMasterServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.router.RouterServer;
import machine.router.RouterConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts up all management services locally, using bot-demo-* as samples.
 */
public class Servers {

    private static final Logger LOG = LoggerFactory.getLogger(Servers.class);

    public static final int MANAGEMENT_SERVER_PORT = 8000;
    public static final int BOT_DEMO_CONSUMER_SERVER_PORT = 8300;
    public static final int BOT_DEMO_MASTER_SERVER_PORT = 8400;

    public void start(LocalServer localServer) throws Exception {
        Thread thread = new Thread(() -> {
            try {
                localServer.start();
            } catch (EmbeddedServerStartException e) {
                throw new IllegalStateException(e);
            }
        }, localServer.getClass().getSimpleName());
        thread.start();
        thread.join();
    }

    public void startConsumer(RouterConfiguration routerConfiguration, Integer index) throws Exception {
        BotDemoConsumerConfiguration botDemoConsumerConfiguration = new BotDemoConsumerConfiguration();
        botDemoConsumerConfiguration.setConfigurationDir(String.format("/etc/backbone-test-%d", index));
        botDemoConsumerConfiguration.setServerPort(BOT_DEMO_CONSUMER_SERVER_PORT + index);
        botDemoConsumerConfiguration.setRouterURL(String.format("http://127.0.0.1:%d", routerConfiguration.getServerPort()));
        BotDemoConsumerServer botDemoConsumerServer = new BotDemoConsumerServer(botDemoConsumerConfiguration);
        start(botDemoConsumerServer);
    }

    @Test
    public void startAll() throws Exception {

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> LOG.error("Thread erred", exception));

        RouterConfiguration routerConfiguration = new RouterConfiguration();
        routerConfiguration.setServerPort(MANAGEMENT_SERVER_PORT);
        RouterServer routerServer = new RouterServer(routerConfiguration);

        BotDemoMasterConfiguration botDemoMasterConfiguration = new BotDemoMasterConfiguration();
        botDemoMasterConfiguration.setServerPort(BOT_DEMO_MASTER_SERVER_PORT);
        botDemoMasterConfiguration.setManagementUrl(String.format("http://127.0.0.1:%d", routerConfiguration.getServerPort()));
        botDemoMasterConfiguration.setHumanitySource("v");
        BotDemoMasterServer botDemoMasterServer = new BotDemoMasterServer(botDemoMasterConfiguration);

        start(routerServer);
        start(botDemoMasterServer);

        for (int index = 0; index < 5; index++) {
            this.startConsumer(routerConfiguration, index);
        }

        Thread.sleep(60000);

    }

}
