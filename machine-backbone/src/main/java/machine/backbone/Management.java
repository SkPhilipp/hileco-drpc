package machine.backbone;

import machine.management.api.services.*;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * Holds all Java proxy clients to a `machine-management` instance.
 */
public class Management {

    public static final String MANAGEMENT_URL = System.getProperty("MANAGEMENT_URL", "http://localhost:8080/services/");

    private static final Management INSTANCE = new Management();

    private DefinitionService definitionService;
    private MessageService messageService;
    private ServerService serverService;
    private SubscriberService subscriberService;
    private TaskService taskService;

    public static Management getInstance() {
        return INSTANCE;
    }

    public Management() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(MANAGEMENT_URL);
        definitionService = target.proxy(DefinitionService.class);
        messageService = target.proxy(MessageService.class);
        serverService = target.proxy(ServerService.class);
        subscriberService = target.proxy(SubscriberService.class);
        taskService = target.proxy(TaskService.class);
    }

    public DefinitionService getDefinitionService() {
        return definitionService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public ServerService getServerService() {
        return serverService;
    }

    public SubscriberService getSubscriberService() {
        return subscriberService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

}
