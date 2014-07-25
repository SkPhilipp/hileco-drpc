package com.hileco.drpc.core.spec;

/**
 * All metadata needed of a message to locate a procedure.
 *
 * @author Philipp Gayret
 */
public class Metadata implements Cloneable {

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
        this.operation = null;
        this.type = Type.CALLBACK;
    }

    /**
     * Constructs MessageMetadata indicating a service operation.
     *
     * @param id        message identifier
     * @param service   reference for a procedure locator
     * @param operation reference to a service's operation
     */
    public Metadata(String id, String service, String operation) {
        this.id = id;
        this.replyTo = null;
        this.service = service;
        this.operation = operation;
        this.type = Type.SERVICE;
    }

    private Type type;
    private String id;
    private String replyTo;
    private String service;
    private String operation;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    @Override
    public String toString() {
        return "{" +
                "type=" + type +
                ", id='" + id + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", service='" + service + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }

}
