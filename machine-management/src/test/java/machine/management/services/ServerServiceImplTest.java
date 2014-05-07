package machine.management.services;

import machine.management.api.entities.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Ignore
public class ServerServiceImplTest {

    private static final String TEST_ADDRESS = "127.0.0.1";

    private ServerServiceImpl service;

    @Before
    public void before() throws Exception {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockedRequest.getRemoteAddr()).thenReturn(TEST_ADDRESS);
        service = new ServerServiceImpl();
        service.setRequest(mockedRequest);
    }

    /**
     * Create an instance, reads it out and asserts:
     * - missing field id is now filled
     * - missing field ipaddress is now filled
     */
    @Test
    public void testCreateRead() throws Exception {
        // create a message, read it and assert missing fields are now filled
        Server instance = new Server();
        instance.setHostname(UUID.randomUUID().toString());
        service.save(instance);
        Server readInstance = service.read(instance.getId());
        Assert.assertNotNull(readInstance.getId());
        Assert.assertEquals(TEST_ADDRESS, readInstance.getIpAddress());
    }

}
