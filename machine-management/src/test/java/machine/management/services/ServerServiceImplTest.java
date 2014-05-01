package machine.management.services;

import machine.management.model.Server;
import machine.management.services.generic.AbstractModelService;
import org.junit.Assert;

import java.util.UUID;

public class ServerServiceImplTest extends AbstractModelServiceTester<Server> {

    private ServerServiceImpl serverService = new ServerServiceImpl();

    @Override
    public void assertEquals(Server expected, Server actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getIpAddress(), actual.getIpAddress());
        Assert.assertEquals(expected.getHostname(), actual.getHostname());
        Assert.assertEquals(expected.getPort(), actual.getPort());
    }

    @Override
    public void randomize(Server original) {
        original.setHostname(UUID.randomUUID().toString());
    }

    @Override
    public AbstractModelService<Server> getModelServiceImpl() {
        return serverService;
    }

}
