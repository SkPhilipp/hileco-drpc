package machine.lib.message.util;

import machine.lib.message.DelegatingMessageService;
import machine.lib.message.MessageHandler;
import machine.message.api.entities.NetworkMessage;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BuiltMessageHandler<T extends Serializable> extends MessageHandler<T> {

    private boolean finished = false;
    private String topic;
    private DelegatingMessageService delegatingMessageService;
    private List<MessageHandler<T>> handlers;
    private List<Runnable> finishListeners;
    private Integer limit;

    /**
     * @param topic the topic to use for unregistering
     * @param delegatingMessageService the delegating message service to use for unregistering
     * @param handlers the message event handlers
     * @param finishListeners the finish event handlers
     * @param limit the maximum amount of messages the event handlers may receive, null indicates not used
     * @param timeoutMillis the maximum time the event handlers may receive, null indicates not used
     */
    public BuiltMessageHandler(final String topic, final DelegatingMessageService delegatingMessageService, List<MessageHandler<T>> handlers, List<Runnable> finishListeners, Integer limit, Long timeoutMillis) {
        this.topic = topic;
        this.finishListeners = finishListeners;
        this.delegatingMessageService = delegatingMessageService;
        this.handlers = handlers;
        this.limit = limit;
        if (timeoutMillis != null) {
            Timer removeTimer = new Timer(true);
            removeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    BuiltMessageHandler.this.finish();
                }
            }, timeoutMillis);
        }
    }

    /**
     * Notifies any item in {@link #handlers} that a message was received. Handles limiting.
     *
     * @param message the message to process
     */
    @Override
    public void handle(NetworkMessage<?> message) {
        boolean doFinish = false;
        if (limit != null && limit > 0) {
            limit--;
            if (limit == 0) {
                doFinish = true;
            }
        }
        if (limit == null || limit >= 0) {
            for (MessageHandler<T> anyHandler : handlers) {
                // TODO: handle in threads & catch exceptions
                anyHandler.handle(message);
            }
        }
        if (doFinish) {
            this.finish();
        }
    }

    /**
     * Notifies any item in {@link #finishListeners} that finish was called.
     *
     * The finish event can only occurs once per instance.
     */
    public void finish() {
        if (!finished) {
            finished = true;
            delegatingMessageService.removeHandler(topic, this);
            for (Runnable finishListener : finishListeners) {
                // TODO: handle in threads & catch exceptions
                finishListener.run();
            }
        }
    }

}
