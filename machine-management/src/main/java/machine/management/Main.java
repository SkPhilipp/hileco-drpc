package machine.management;

import com.google.common.primitives.Ints;
import machine.lib.service.EmbeddedServer;
import machine.management.services.*;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "80"));

    public static void main(String[] args) throws Exception {
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(new DefinitionServiceImpl());
        services.add(new MessageServiceImpl());
        services.add(new ServerServiceImpl());
        services.add(new SubscriberServiceImpl());
        services.add(new TaskServiceImpl());
        embeddedServer.start(services);
    }

}
