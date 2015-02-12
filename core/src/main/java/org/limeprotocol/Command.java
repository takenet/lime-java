package org.limeprotocol;

import java.util.UUID;

public class Command extends Envelope {

    public Command() {
    }

    public Command(UUID id) {
        super(id);
    }

    /**
     * The universal identifier
     * of the resource
     */
    private LimeUri uri;

    /**
     *  Server resource that are subject
     *  of the command
     */
    private Document resource;

    /**
     *  Action to be taken to the
     *  resource
     */
    private CommandMethod method;

    /**
     *  Indicates the status of
     *  the action taken to the resource
     */
    private CommandStatus status;

    /**
     *  Indicates a reason for
     *  the status
     */
    private Reason reason;

    public LimeUri getUri() {
        return uri;
    }

    public void setUri(LimeUri uri) {
        this.uri = uri;
    }

    public MediaType getType() {
        if (this.resource != null) {
            return this.resource.getMediaType();
        }

        return null;
    }

    public Document getResource() {
        return resource;
    }

    public void setResource(Document resource) {
        this.resource = resource;
    }

    public CommandMethod getMethod() {
        return method;
    }

    public void setMethod(CommandMethod method) {
        this.method = method;
    }

    public CommandStatus getStatus() {
        return status;
    }

    public void setStatus(CommandStatus status) {
        this.status = status;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    /**
     * Defines method for the manipulation
     * of messaging.
     */
    public enum CommandMethod {
        /**
         * Gets an existing value of the resource.
         */
        GET,

        /**
         * Sets or updates a for the resource.
         */
        SET,

        /**
         * Deletes a value of the resource
         * or the resource itself.
         */
        DELETE,

        /**
         * Notify the destination about a change
         * in the resource value of the sender.
         * This method is one way and the destination
         * SHOULD NOT send a response for it.
         * Because of that, a command envelope with this
         * method MAY NOT have an id.
         */
        OBSERVE
    }

    /**
     * Represents the status of a resource operation.
     */
    public enum CommandStatus {
        /**
         * The resource action has been successfully executed.
         */
        SUCCESS,
        /**
         * The resource action has failed.
         */
        FAILURE
    }


}
