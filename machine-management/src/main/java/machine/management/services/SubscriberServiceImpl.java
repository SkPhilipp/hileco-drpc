package machine.management.services;

import machine.management.domain.Subscriber;
import machine.lib.service.dao.GenericModelDAO;
import machine.lib.service.services.AbstractQueryableModelService;

import javax.ws.rs.Path;

@Path("/subscribers")
public class SubscriberServiceImpl extends AbstractQueryableModelService<Subscriber> {

    private static final GenericModelDAO<Subscriber> DAO = new GenericModelDAO<>(Subscriber.class);

    public SubscriberServiceImpl() {
        super(DAO);
    }

}
