package machine.management.services;

import machine.management.domain.Server;
import machine.management.services.helpers.AbstractQueryableModelServiceTester;
import machine.management.services.lib.services.AbstractQueryableModelService;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class ServerServiceImplTest extends AbstractQueryableModelServiceTester<Server> {

    private static final String TEST_ADDRESS = "127.0.0.1";
    private ServerServiceImpl serverService;

    @Before
    public void before() throws Exception {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockedRequest.getRemoteAddr()).thenReturn(TEST_ADDRESS);
        serverService = new ServerServiceImpl(mockedRequest);
    }

    @Override
    public void assertEquals(Server expected, Server actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getIpAddress(), TEST_ADDRESS);
        Assert.assertEquals(expected.getHostname(), actual.getHostname());
        Assert.assertEquals(expected.getPort(), actual.getPort());
    }

    @Override
    public void randomizeModel(Server original) {
        original.setHostname(UUID.randomUUID().toString());
    }

    @Override
    public AbstractQueryableModelService<Server> getQueryableModelServiceImpl() {
        return serverService;
    }

}
