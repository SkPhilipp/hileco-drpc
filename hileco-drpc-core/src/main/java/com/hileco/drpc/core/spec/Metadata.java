package com.hileco.drpc.core.spec;

import java.util.List;

/**
 * Immutable metadata containing all required information on how to process a message.
 *
 * @author Philipp Gayret
 */
public class Metadata {

    public static final String CALLBACK_TOPIC = "CALLBACK_TOPIC";

    public static enum Type {
        SERVICE, CALLBACK
    }

    /**
     * Constructs MessageMetadata indicating a callback.
     *
     * @param id      message identifier
     * @param replyTo identifier of message to reply to
     */
    public Metadata(String id, String replyTo) {
        this.id = id;
        this.replyTo = replyTo;
        this.service = null;
        this.targets = null;
        this.operation = null;
        this.type = Type.CALLBACK;
        this.expectResponse = false;
    }

    /**
     * Constructs MessageMetadata indicating a service operation.
     *
     * @param id        message identifier
     * @param service   reference for a procedure locator
     * @param operation reference to a service's operation
     * @param targets   list of service hosts by registered identifier, or null if to send to all
     */
    public Metadata(String id, String service, String operation, List<String> targets) {
        this.id = id;
        this.replyTo = null;
        this.targets = targets;
        this.service = service;
        this.operation = operation;
        this.type = Type.SERVICE;
        this.expectResponse = true;
    }

    private final Type type;
    private final String id;
    private final List<String> targets;
    private final String replyTo;
    private final String service;
    private final String operation;
    private final Boolean expectResponse;

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public String getService() {
        return service;
    }

    public String getOperation() {
        return operation;
    }

    public Boolean getExpectResponse() {
        return expectResponse;
    }

    public Boolean hasTargets() {
        return targets != null;
    }

    public List<String> getTargets() {
        return targets;
    }

    public String getTopic() {
        switch (this.type) {
            case SERVICE:
                return service;
            case CALLBACK:
                return replyTo;
            default:
                return null;
        }
    }

}
