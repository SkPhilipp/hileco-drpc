package machine.management.domain;

public enum TaskStatus {

    /**
     * Indicates a task is yet to be processed by a Master.
     */
    INACTIVE,
    /**
     * Indicates a task is being processed
     */
    ACTIVE,
    /**
     * Indicates the task could not be processed
     */
    ERRED,
    /**
     * Indicates the task has been processed successfully
     */
    DONE

}
