package machine.management.services.impl;

import com.google.common.base.Preconditions;
import machine.management.model.Task;
import machine.management.model.TaskStatus;
import machine.management.services.TaskService;

import javax.ws.rs.Path;
import java.util.List;
import java.util.UUID;

@Path("/tasks")
public class TaskServiceImpl extends AbstractModelService<Task> implements TaskService {

    @Override
    public UUID create(Task instance){
        Preconditions.checkNotNull(instance.getType(), "The property of the given task must not be null.");
        Preconditions.checkNotNull(instance.getBottable(), "The bottable property of the given task must not be null.");
        instance.setStatus(TaskStatus.INACTIVE);
        return super.create(instance);
    }

    @Override
    public List<Task> findAllByFilters(String bottableFilter, String typeFilter, TaskStatus statusFilter) {
        return null; //TODO: implement
    }

    @Override
    public List<Task> findAllByStatus(TaskStatus statusFilter) {
        return null; //TODO: implement
    }

}
