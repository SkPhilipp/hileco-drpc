package machine.management.api.services;

import machine.lib.service.services.QueryableModelService;
import machine.management.api.domain.Task;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TaskService extends QueryableModelService<Task> {
}
