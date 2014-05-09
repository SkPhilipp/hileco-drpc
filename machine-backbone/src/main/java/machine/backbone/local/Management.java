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

    private static final List<?> providers = Collections.singletonList(new JacksonJsonProvider());

    private DefinitionService definitionService;
    private MessageService messageService;
    private ServerService serverService;
    private SubscriberService subscriberService;
    private TaskService taskService;

    /**
     * Instantiates a Management object, creates all service proxies.
     *
     * @param managementURL full url to managenent, i.e. "http://localhost:80/"
     */
    public Management(String managementURL) {
        this.setURL(managementURL);
    }

    /**
     * Initializes all client proxies, point them to the given management URL.
     *
     * @param managementURL where all the services are.
     */
    public void setURL(String managementURL){
        definitionService = JAXRSClientFactory.create(managementURL, DefinitionService.class, providers);
        messageService = JAXRSClientFactory.create(managementURL, MessageService.class, providers);
        serverService = JAXRSClientFactory.create(managementURL, ServerService.class, providers);
        subscriberService = JAXRSClientFactory.create(managementURL, SubscriberService.class, providers);
        taskService = JAXRSClientFactory.create(managementURL, TaskService.class, providers);
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
