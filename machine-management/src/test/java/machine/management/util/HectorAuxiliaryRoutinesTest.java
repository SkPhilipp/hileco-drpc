package machine.management.util;

import machine.management.model.Server;
import me.prettyprint.hom.EntityManagerImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

/**
 * Testing your setup of Cassandra with Hector.
 */
public class HectorAuxiliaryRoutinesTest {

    @Ignore
    @Test
    public void test() {

        HectorAuxiliaryRoutines hectorAuxiliaryRoutines = HectorAuxiliaryRoutines.getInstance();
        hectorAuxiliaryRoutines.addColumnFamilyIfNotExists(Server.class.getSimpleName());
        EntityManagerImpl entityManager = hectorAuxiliaryRoutines.getEntityManager();

        try {

            // create something
            Server server = new Server();
            server.setHostname("hello");
            server.setId(UUID.randomUUID());
            server.setIpAddress("127.0.0.1");
            server.setPort(80);
            entityManager.persist(server);
            // get it out again
            Server server1 = entityManager.find(Server.class, server.getId());
            System.out.println("IpAddress = " + server1.getIpAddress());
            // at this point you may celebrate not having exceptions all over the place

        } finally {
            hectorAuxiliaryRoutines.getCluster().getConnectionManager().shutdown();
        }

    }

}
