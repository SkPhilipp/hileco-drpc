package machine.backbone.local;

import machine.management.api.services.*;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import java.util.Collections;
import java.util.List;

/**
 * Holds all clients to external `machine` services.
 */
public class Management {

    private DefinitionService definitionService;
    private MessageService messageService;
    private ServerService serverService;
    private SubscriberService subscriberService;
    private TaskService taskService;

    /**
     * Instantiates a Management object, creates all service proxies.
     *
     * @param managementUrl full url to managenent, i.e. "http://localhost:80/"
     */
    @SuppressWarnings("deprecation")
    public Management(String managementUrl) {
        List<?> providers = Collections.singletonList(new JacksonJsonProvider());
        definitionService = JAXRSClientFactory.create(managementUrl, DefinitionService.class, providers);
        messageService = JAXRSClientFactory.create(managementUrl, MessageService.class, providers);
        serverService = JAXRSClientFactory.create(managementUrl, ServerService.class, providers);
        subscriberService = JAXRSClientFactory.create(managementUrl, SubscriberService.class, providers);
        taskService = JAXRSClientFactory.create(managementUrl, TaskService.class, providers);
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
