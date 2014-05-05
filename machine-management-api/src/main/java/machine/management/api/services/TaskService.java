package machine.management.api.services;

import machine.lib.service.services.QueryableModelService;
import machine.management.api.domain.Task;

import javax.ws.rs.Path;

@Path("/tasks")
public interface TaskService extends QueryableModelService<Task> {
}
