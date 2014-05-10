package machine.management.services;

import machine.management.dao.GenericModelDAO;
import machine.management.api.services.TaskService;
import machine.management.api.entities.Task;

import javax.ws.rs.Path;

@Path("/tasks")
public class TaskServiceImpl extends AbstractQueryableModelService<Task> implements TaskService {

    private static final GenericModelDAO<Task> DAO = new GenericModelDAO<>(Task.class);

    public TaskServiceImpl() {
        super(DAO);
    }

}
