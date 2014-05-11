package bot.demo.consumer;

import bot.demo.consumer.services.MessageServiceImpl;
import com.google.common.primitives.Ints;
import machine.lib.service.EmbeddedServer;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "8080"));

    public static void main(String[] args) throws Exception {
        // run the server
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(new MessageServiceImpl());
        embeddedServer.start(services);
    }

}
