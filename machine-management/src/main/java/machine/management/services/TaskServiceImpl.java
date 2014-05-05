package machine.management.services;

import machine.management.domain.Task;
import machine.management.services.lib.dao.GenericModelDAO;
import machine.management.services.lib.services.AbstractQueryableModelService;

import javax.ws.rs.Path;

@Path("/tasks")
public class TaskServiceImpl extends AbstractQueryableModelService<Task> {

    private static final GenericModelDAO<Task> DAO = new GenericModelDAO<>(Task.class);

    public TaskServiceImpl() {
        super(DAO);
    }

}
