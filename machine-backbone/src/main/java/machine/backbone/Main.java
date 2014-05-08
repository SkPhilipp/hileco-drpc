package machine.backbone;

import com.google.common.primitives.Ints;
import machine.backbone.local.Installer;
import machine.backbone.services.RemoteCommandServiceImpl;
import machine.lib.service.EmbeddedServer;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "80"));

    public static void main(String[] args) throws Exception {
        // initially, perform installation
        Installer installer = Installer.getInstance();
        installer.install(SERVER_PORT);
        // run the server
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(new RemoteCommandServiceImpl());
        embeddedServer.start(services);
    }

}
