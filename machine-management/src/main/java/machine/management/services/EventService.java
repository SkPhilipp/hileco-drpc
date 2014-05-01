package machine.management.services;

import machine.events.model.Subscriber;

import java.util.List;

public interface EventService {

    /**
     * Subscribes the API client on a topic by the client's IP address, port parameter is optional and will be defaulted when omitted.
     * Messages published on the topic will be sent to the client's IP address &amp; given port combination.
     *
     * @param topic
     * @param port
     */
    public void subscribe(String topic, Integer port);

    /**
     * Unsubscribes the API client from a topic, by the client's IP address.
     *
     * @param topic
     */
    public void unsubscribe(String topic);

    /**
     * Retrieves the list of subscribers to a topic.
     *
     * @param topic
     * @return
     */
    public List<Subscriber> subscribers(String topic);

    /**
     * Publishes the request as a message to all subscribers for the given topic.
     *
     * @param topic
     * @param message
     */
    public void publish(String topic, String message);

}
