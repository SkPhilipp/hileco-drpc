package machine.backbone;

import machine.backbone.local.LocalConfiguration;
import machine.backbone.local.LocalInstaller;
import machine.backbone.local.Management;
import machine.backbone.processes.HeartbeatProcess;
import machine.backbone.services.RemoteCommandServiceImpl;
import machine.backbone.services.RemoteManagementImpl;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;

import java.util.HashSet;
import java.util.Set;

public class BackboneServer implements LocalServer {

    private final BackboneConfiguration configuration;

    public BackboneServer(BackboneConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws EmbeddedServerStartException {
        BackboneConfiguration backboneConfiguration = new BackboneConfiguration();
        Config.set("CONFIGURATION_DIR", "/etc/machine-backbone.conf.d", backboneConfiguration::setConfigurationDir);
        Config.set("MANAGEMENT_URL", "http://localhost:80/", backboneConfiguration::setDefaultManagementUrl);
        Config.set("SERVER_PORT", 82, backboneConfiguration::setDefaultServerPort);
        Config.set("HEARTBEAT_PERIOD", 10, backboneConfiguration::setDefaultHeartbeatPeriod);
        BackboneServer backboneServer = new BackboneServer(backboneConfiguration);
        backboneServer.start();
    }

    public void start() throws EmbeddedServerStartException {

        LocalConfiguration localConfiguration = new LocalConfiguration(configuration.getConfigurationDir());
        LocalInstaller localInstaller = new LocalInstaller(localConfiguration);
        Management management = localInstaller.install(configuration.getDefaultManagementUrl(), configuration.getDefaultServerPort());

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getDefaultServerPort());
        Set<Object> services = new HashSet<>();
        services.add(new RemoteCommandServiceImpl());
        services.add(new RemoteManagementImpl(management, localConfiguration));
        embeddedServer.start(services);

        HeartbeatProcess heartbeatProcess = new HeartbeatProcess(management.getServerService(), localConfiguration);
        heartbeatProcess.schedule(configuration.getDefaultHeartbeatPeriod());

    }

}
