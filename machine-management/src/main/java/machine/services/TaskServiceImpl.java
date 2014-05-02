package machine.services;

import machine.management.model.Task;
import machine.services.lib.services.AbstractQueryableModelService;

import javax.ws.rs.Path;

@Path("/tasks")
public class TaskServiceImpl extends AbstractQueryableModelService<Task> {

}
