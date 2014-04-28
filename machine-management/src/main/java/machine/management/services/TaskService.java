package machine.management.services;

import machine.management.model.Task;
import machine.management.model.TaskStatus;

import java.util.List;

public interface TaskService extends ModelService<Task> {

    /**
     * Lists a filtered set of tasks.
     *
     * @param bottableFilter filter to use for the {@link machine.management.model.Task#bottable} property
     * @param typeFilter filter to use for the {@link machine.management.model.Task#type} property
     * @param statusFilter optional filter to use for the {@link machine.management.model.Task#status} property
     * @return all matching definitions
     */
    public List<Task> findAllByFilters(String bottableFilter, String typeFilter, TaskStatus statusFilter);

    /**
     * Lists a filtered set of tasks.
     *
     * @param statusFilter optional ( default; {@link TaskStatus#INACTIVE} ) filter to use for the {@link machine.management.model.Task#status} property
     * @return all matching definitions
     */
    public List<Task> findAllByStatus(TaskStatus statusFilter);

}
