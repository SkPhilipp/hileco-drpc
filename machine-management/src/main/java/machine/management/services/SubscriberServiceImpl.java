package machine.management.services;

import machine.management.domain.Subscriber;
import machine.management.services.lib.dao.GenericModelDAO;
import machine.management.services.lib.services.AbstractModelService;

import javax.ws.rs.Path;

@Path("/subscribers")
public class SubscriberServiceImpl extends AbstractModelService<Subscriber> {

    private static final GenericModelDAO<Subscriber> DAO = new GenericModelDAO<Subscriber>(Subscriber.class);

    public SubscriberServiceImpl() {
        super(DAO);
    }

}
