package machine.management.processes.events;

import machine.management.domain.Event;
import machine.management.domain.Message;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Performs the actual sending of messages.
 */
public class EventRouter {

    private static final Logger LOG = LoggerFactory.getLogger(EventRouter.class);
    private static final int REQUEST_TIMEOUT = 5000;
    private static final int SENDER_POOL_SIZE = 100;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final HttpClient httpClient;
    private final ScheduledExecutorService executorService;

    public EventRouter() {
        this.executorService = Executors.newScheduledThreadPool(SENDER_POOL_SIZE);
        RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT)
                .setConnectTimeout(REQUEST_TIMEOUT)
                .setSocketTimeout(REQUEST_TIMEOUT)
                .build();
        this.httpClient = HttpClients.custom()
                .setMaxConnTotal(SENDER_POOL_SIZE)
                .setDefaultRequestConfig(config)
                .build();
    }

    public Future<HttpResponse> submit(final Event event, final Message message) {
        return this.executorService.submit(new Callable<HttpResponse>() {
            @Override
            public HttpResponse call() throws Exception {
                String body = OBJECT_MAPPER.writeValueAsString(message);
                StringEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
                HttpPost request = new HttpPost(event.getTarget());
                request.setEntity(stringEntity);
                LOG.debug("Performing a request against: {}", event.getTarget());
                return httpClient.execute(request);
            }
        });
    }

    public void shutdown() {
        LOG.debug("Shutting down executor service which handles request");
        executorService.shutdown();
    }

}
