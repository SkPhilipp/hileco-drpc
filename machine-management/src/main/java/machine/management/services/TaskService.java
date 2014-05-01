package machine.management.services;

import machine.management.model.Task;
import machine.management.services.generic.QueryableModelService;

import javax.ws.rs.Path;

@Path("/tasks")
public interface TaskService extends QueryableModelService<Task> {

}
