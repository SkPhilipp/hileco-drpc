package bot.demo.master;

import bot.demo.messages.ScanReply;
import bot.demo.messages.Topics;
import machine.management.api.entities.Subscriber;
import machine.management.api.services.SubscriberService;
import machine.message.api.entities.NetworkMessage;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Sample {

    public static final String MANAGEMENT_URL = "http://localhost:80/";
    public static final String SELF_URL = "http://localhost:8080/";
    private static final Logger LOG = LoggerFactory.getLogger(Sample.class);

    public static void main(String[] args) {
        LOG.info("Starting!");

        SubscriberService subscriberService = JAXRSClientFactory.create(MANAGEMENT_URL, SubscriberService.class);

        NetworkMessage<String> scanMessage = new NetworkMessage<>(Topics.SCAN, "");

        Subscriber callback = new Subscriber();
        callback.setTarget(SELF_URL);
        callback.setTopic(scanMessage.getMessageId().toString());

        subscriberService.save(callback);
        subscriberService.publish(scanMessage); // <-- TODO: do some handler function abstraction here

        // then wait while actually listening on SELF_URL (...) for callbacks ...
        // ok now we can continue and pretend we got some responses

        List<NetworkMessage<ScanReply>> replies = new ArrayList<>();

        // now we can call machine-humanity and send out some things for the scan-replied machines to do
    }

}
