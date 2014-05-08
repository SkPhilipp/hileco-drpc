package machine.backbone;

import com.google.common.primitives.Ints;
import machine.backbone.local.Installer;
import machine.backbone.services.RemoteCommandServiceImpl;
import machine.lib.service.EmbeddedServer;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static final String DEFAULT_MANAGEMENT_URL = System.getProperty("DEFAULT_MANAGEMENT_URL", "http://localhost:80/");
    public static final Integer DEFAULT_SERVER_PORT = Ints.tryParse(System.getProperty("DEFAULT_SERVER_PORT", "82"));

    public static void main(String[] args) throws Exception {
        // initially, perform installation
        Installer installer = new Installer();
        installer.install(DEFAULT_MANAGEMENT_URL, DEFAULT_SERVER_PORT);
        // run the server
        EmbeddedServer embeddedServer = new EmbeddedServer(DEFAULT_SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(new RemoteCommandServiceImpl());
        embeddedServer.start(services);
    }

}
