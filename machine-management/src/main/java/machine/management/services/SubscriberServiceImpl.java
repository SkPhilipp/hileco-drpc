package machine.management.services;

import machine.management.dao.GenericModelDAO;
import machine.management.api.services.SubscriberService;
import machine.management.api.entities.Subscriber;

import javax.ws.rs.Path;

@Path("/subscribers")
public class SubscriberServiceImpl extends AbstractQueryableModelService<Subscriber> implements SubscriberService {

    private static final GenericModelDAO<Subscriber> DAO = new GenericModelDAO<>(Subscriber.class);

    public SubscriberServiceImpl() {
        super(DAO);
    }

}
