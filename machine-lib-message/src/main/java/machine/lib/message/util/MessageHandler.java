package machine.lib.message.util;

import machine.lib.message.LoadedTypedMessage;
import machine.lib.message.api.Network;
import machine.lib.message.TypedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MessageHandler<T extends Serializable> implements Consumer<TypedMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(MessageHandler.class);
    private boolean finished = false;
    private Class<T> messageClass;
    private String topic;
    private Network network;
    private List<Consumer<LoadedTypedMessage<T>>> handlers;
    private List<Runnable> finishListeners;
    private Integer limit;

    /**
     * @param messageClass    the message's class type
     * @param topic           the topic to use for unregistering
     * @param network         the network to stop listen
     * @param handlers        the message event handlers
     * @param finishListeners the finish event handlers
     * @param limit           the maximum amount of messages the event handlers may receive, null indicates not used
     * @param timeoutMillis   the maximum time the event handlers may receive, null indicates not used
     */
    public MessageHandler(Class<T> messageClass, final String topic, final Network network, List<Consumer<LoadedTypedMessage<T>>> handlers, List<Runnable> finishListeners, Integer limit, Long timeoutMillis) {
        this.messageClass = messageClass;
        this.topic = topic;
        this.finishListeners = finishListeners;
        this.network = network;
        this.handlers = handlers;
        this.limit = limit;
        if (timeoutMillis != null) {
            Timer removeTimer = new Timer(true);
            removeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MessageHandler.this.finish();
                }
            }, timeoutMillis);
        }
    }

    /**
     * Notifies any item in {@link #handlers} that a message was received. Handles limiting.
     *
     * @param typedMessage the message to process
     */
    @Override
    public void accept(TypedMessage typedMessage) {
        boolean doFinish = false;
        if (limit != null && limit > 0) {
            limit--;
            if (limit == 0) {
                doFinish = true;
            }
        }
        if (limit == null || limit >= 0) {
            LoadedTypedMessage<T> loadedTypedMessage = new LoadedTypedMessage<>(typedMessage.getNetworkMessage(), this.messageClass);
            handlers.parallelStream().forEach((processor) -> {
                try {
                    processor.accept(loadedTypedMessage);
                } catch (Exception e) {
                    LOG.error("A processor erred while handling a message", e);
                }
            });
        }
        if (doFinish) {
            this.finish();
        }
    }

    /**
     * Notifies any item in {@link #finishListeners} that finish was called.
     * <p/>
     * The finish event can only occurs once per instance.
     */
    public void finish() {
        if (!finished) {
            finished = true;
            network.stopListen(topic, this);
            finishListeners.parallelStream().forEach((listener) -> {
                try {
                    listener.run();
                } catch (Exception e) {
                    LOG.error("A processor erred while handling a message", e);
                }
            });
        }
    }

}
