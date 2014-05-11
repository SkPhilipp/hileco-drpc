package bot.demo.consumer.services;

import bot.demo.messages.Topics;
import machine.message.api.entities.NetworkMessage;
import machine.message.api.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageServiceImpl implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Override
    public void handle(NetworkMessage<?> instance) {
        if(Topics.SCAN.equals(instance.getTopic())){
            // Parse the content
        }
        else{
            LOG.warn("Received a message with topic: {}", instance.getTopic());
        }
    }

}
