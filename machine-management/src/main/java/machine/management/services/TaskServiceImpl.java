package machine.management.services;

import machine.lib.service.dao.GenericModelDAO;
import machine.lib.service.services.AbstractQueryableModelService;
import machine.management.api.services.TaskService;
import machine.management.api.domain.Task;

import javax.ws.rs.Path;

@Path("/tasks")
public class TaskServiceImpl extends AbstractQueryableModelService<Task> implements TaskService {

    private static final GenericModelDAO<Task> DAO = new GenericModelDAO<>(Task.class);

    public TaskServiceImpl() {
        super(DAO);
    }

}
