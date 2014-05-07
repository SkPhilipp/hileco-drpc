package machine.management.services.messaging;

import com.google.common.base.Charsets;
import machine.management.api.entities.NetworkMessage;
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

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Performs the actual sending of messages.
 */
public class NetworkMessageRouter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static Charset CHARSET = Charsets.UTF_8;
    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;
    public static final int DEFAULT_SENDER_POOL_SIZE = 100;

    private static final Logger LOG = LoggerFactory.getLogger(NetworkMessageRouter.class);

    private final HttpClient httpClient;
    private final ScheduledExecutorService executorService;

    public NetworkMessageRouter(int senderPoolSize, int requestTimeout){
        this.executorService = Executors.newScheduledThreadPool(senderPoolSize);
        RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT)
                .setConnectTimeout(requestTimeout)
                .setSocketTimeout(requestTimeout)
                .build();
        this.httpClient = HttpClients.custom()
                .setMaxConnTotal(DEFAULT_SENDER_POOL_SIZE)
                .setDefaultRequestConfig(config)
                .build();
    }

    /**
     * Sets up the message router with a default request timeout of {@link #DEFAULT_REQUEST_TIMEOUT} and pool size of {@link #DEFAULT_SENDER_POOL_SIZE}.
     */
    public NetworkMessageRouter() {
        this(DEFAULT_SENDER_POOL_SIZE, DEFAULT_REQUEST_TIMEOUT);
    }

    /**
     * Sends a {@link NetworkMessage} to any given HTTP target as JSON.
     *
     * @param target receiver url of message
     * @param networkMessage message to be sent
     * @param <T> type of message content
     * @return future of the receiver's response
     */
    public <T extends Serializable> Future<HttpResponse> submit(final String target, final NetworkMessage<T> networkMessage) {
        return this.executorService.submit(new Callable<HttpResponse>() {
            @Override
            public HttpResponse call() throws Exception {
                String body = OBJECT_MAPPER.writeValueAsString(networkMessage);
                StringEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
                HttpPost request = new HttpPost(target);
                request.setEntity(stringEntity);
                LOG.debug("Performing a request against: {}", target);
                return httpClient.execute(request);
            }
        });
    }

    /**
     * Sends a serialized {@link NetworkMessage} to any given HTTP target as JSON.
     *
     * @param target receiver url of message
     * @param networkMessage serialized message to be sent
     * @param <T> type of message content
     * @return future of the receiver's response
     */
    public <T extends Serializable> Future<HttpResponse> submit(final String target, final String networkMessage) {
        return this.executorService.submit(new Callable<HttpResponse>() {
            @Override
            public HttpResponse call() throws Exception {
                StringEntity stringEntity = new StringEntity(networkMessage, ContentType.APPLICATION_JSON);
                HttpPost request = new HttpPost(target);
                request.setEntity(stringEntity);
                LOG.debug("Performing a request against: {}", target);
                return httpClient.execute(request);
            }
        });
    }

    /**
     * Shuts down the executor service handling requests.
     */
    public void shutdown() {
        LOG.debug("Shutting down executor service which handles request");
        executorService.shutdown();
    }

}
