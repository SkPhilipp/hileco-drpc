package machine.humanity;

import com.google.common.primitives.Ints;
import machine.humanity.services.GeneratorServiceImpl;
import machine.lib.service.EmbeddedServer;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "81"));

    public static void main(String[] args) throws Exception {
        // run the server
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(new GeneratorServiceImpl());
        embeddedServer.start(services);
    }

}
