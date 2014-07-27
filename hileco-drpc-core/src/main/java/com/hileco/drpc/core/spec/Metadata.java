package com.hileco.drpc.core.spec;

import java.util.List;

/**
 * All metadata needed of a message to locate a procedure.
 *
 * @author Philipp Gayret
 */
public class Metadata implements Cloneable {

    public static enum Type {
        SERVICE, CALLBACK
    }

    public Metadata() {
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
        this.expectResponse = true;
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

    private Type type;
    private String id;
    private List<String> targets;
    private String replyTo;
    private String service;
    private String operation;
    private Boolean expectResponse;

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

    public Boolean getExpectResponse() {
        return expectResponse;
    }

    public void setExpectResponse(Boolean expectResponse) {
        this.expectResponse = expectResponse;
    }

    public Boolean hasTargets() {
        return targets != null;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
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
        return "Metadata{" +
                "type=" + type +
                ", id='" + id + '\'' +
                ", targets=" + targets +
                ", replyTo='" + replyTo + '\'' +
                ", service='" + service + '\'' +
                ", operation='" + operation + '\'' +
                ", expectResponse=" + expectResponse +
                '}';
    }

}
