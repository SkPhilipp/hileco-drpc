package machine.drcp.http.impl;

import com.google.common.base.Preconditions;
import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.services.RouterService;
import machine.drcp.http.api.JaxrsMessageService;
import machine.drcp.http.api.JaxrsRouterService;
import machine.drcp.http.api.models.HTTPSubscription;
import machine.drcp.http.impl.store.LocalSubscriptionStore;
import machine.drcp.http.impl.store.SubscriptionStore;
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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Router implements JaxrsRouterService {

    public static final int DEFAULT_SENDER_POOL_SIZE = 100;
    public static final int DEFAULT_REQUEST_TIMEOUT = 2000;

    private static final Logger LOG = LoggerFactory.getLogger(Router.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SubscriptionStore subscriptionStore;
    private final HttpClient httpClient;
    private final ScheduledExecutorService executorService;
    @Context
    private HttpServletRequest request;

    public Router(SubscriptionStore subscriptionStore) {
        this.subscriptionStore = subscriptionStore;
        this.executorService = Executors.newScheduledThreadPool(DEFAULT_SENDER_POOL_SIZE);
        RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT)
                .setConnectTimeout(DEFAULT_REQUEST_TIMEOUT)
                .setSocketTimeout(DEFAULT_REQUEST_TIMEOUT)
                .build();
        this.httpClient = HttpClients.custom()
                .setMaxConnTotal(DEFAULT_SENDER_POOL_SIZE)
                .setDefaultRequestConfig(config)
                .build();
    }

    /**
     * Instantiates the {@link machine.drcp.http.impl.Router} using a {@link machine.drcp.http.impl.store.LocalSubscriptionStore}.
     */
    public Router() {
        this(new LocalSubscriptionStore());
    }

    @Override
    public void publish(Message message) {
        Preconditions.checkArgument(message.getTopic() != null, "Topic must not be empty");
        Collection<HTTPSubscription> httpSubscriptions = subscriptionStore.withTopic(message.getTopic());
        try {
            String body = OBJECT_MAPPER.writeValueAsString(message);
            final StringEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
            for (HTTPSubscription httpSubscription : httpSubscriptions) {
                this.executorService.submit(() -> {
                    String target = String.format("http://%s:%d/message/?%s=%s", httpSubscription.getHost(), httpSubscription.getPort(), JaxrsMessageService.SUBSCRIPTION_ID, httpSubscription.getId().toString());
                    HttpPost request = new HttpPost(target);
                    request.setEntity(stringEntity);
                    try {
                        HttpResponse response = httpClient.execute(request);
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == 400) {
                            LOG.debug("Request completed against {}, status code indicates subscription {} must be deleted.", target, httpSubscription.getId());
                            this.delete(httpSubscription.getId());
                        } else {
                            LOG.debug("Sent message with topic {} subscription {} to {}.", message.getTopic(), httpSubscription.getId(), target);
                        }
                    } catch (ConnectException e) {
                        LOG.warn("Unable to connect to subscribed receiver {}", target);
                    } catch (Throwable e) {
                        LOG.warn("Erred while sending to a subscribed receiver", e);
                    }
                });
            }
        } catch (IOException e) {
            LOG.error("Unable to serialize a networkmessage, with id {} and topic {}", message.getId(), message.getTopic());
        }
    }

    @Override
    public void extend(UUID id) {
        HTTPSubscription subscription = subscriptionStore.read(id);
        if (subscription != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, RouterService.DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS);
            Date date = calendar.getTime();
            subscription.setExpires(date);
            this.save(subscription);
        } else {
            LOG.warn("An attempt was made by a client to extend a subscription with id {}, but no subscription was found for it.", id);
        }
    }

    @Override
    public HTTPSubscription save(HTTPSubscription instance) {
        Preconditions.checkArgument(instance.getHost() == null, "Clients are not permitted to provide the IP-address themselves.");
        Preconditions.checkNotNull(instance.getTopic(), "Clients must provide a topic.");
        Preconditions.checkNotNull(instance.getPort(), "Clients must provide a port.");
        String clientIpAddress = this.request.getRemoteAddr();
        instance.setHost(clientIpAddress);
        if (instance.getExpires() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, RouterService.DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS);
            Date date = calendar.getTime();
            instance.setExpires(date);
        }
        return subscriptionStore.save(instance);
    }

    @Override
    public void delete(UUID id) {
        subscriptionStore.delete(id);
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

}
