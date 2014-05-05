package machine.management.services;

import machine.management.domain.Task;
import machine.lib.service.dao.GenericModelDAO;
import machine.lib.service.services.AbstractQueryableModelService;

import javax.ws.rs.Path;

@Path("/tasks")
public class TaskServiceImpl extends AbstractQueryableModelService<Task> {

    private static final GenericModelDAO<Task> DAO = new GenericModelDAO<>(Task.class);

    public TaskServiceImpl() {
        super(DAO);
    }

}
