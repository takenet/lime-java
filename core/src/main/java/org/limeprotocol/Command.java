package org.limeprotocol;

import java.util.UUID;

/**
 * Allows the manipulation of node resources, like server session parameters 
 * * or information related to the network nodes.
 */
public class Command extends Envelope {

    private LimeUri uri;
    private MediaType type;
    private Document resource;
    private CommandMethod method;
    private CommandStatus status;
    private Reason reason;
    
    public Command() {
    }

    public Command(UUID id) {
        super(id);
    }
    
    /**
     * Gets the universal identifier of the resource.
     * @return
     */
    public LimeUri getUri() {
        return uri;
    }

    /**
     * Sets the universal identifier of the resource.
     */
    public void setUri(LimeUri uri) {
        this.uri = uri;
    }

    /**
     * Gets the MIME declaration of the resource type of the command.
     */
    public MediaType getType() {
        if (this.resource != null) {
            return this.resource.getMediaType();
        }

        return null;
    }

    /**
     * Sets the MIME declaration of the resource type of the command.
     * @param type
     */
    public void setType(MediaType type) {
        this.type = type;
    }
    
    /**
     *  Gets the server resource that are subject of the command.
     */
    public Document getResource() {
        return resource;
    }

    /**
     *  Sets the server resource that are subject of the command.
     */
    public void setResource(Document resource) {
        this.resource = resource;
    }

    /**
     *  Gets the action to be taken to the resource.
     */
    public CommandMethod getMethod() {
        return method;
    }

    /**
     *  Sets the action to be taken to the resource.
     */
    public void setMethod(CommandMethod method) {
        this.method = method;
    }

    /**
     *  Gets the indicator that the status of the action taken to the resource.
     */
    public CommandStatus getStatus() {
        return status;
    }

    /**
     *  Sets the indicator that the status of the action taken to the resource.
     */
    public void setStatus(CommandStatus status) {
        this.status = status;
    }

    /**
     *  Gets the indicator of the reason for the status.
     */
    public Reason getReason() {
        return reason;
    }

    /**
     *  Sets the indicator of the reason for the status.
     */
    public void setReason(Reason reason) {
        this.reason = reason;
    }
    
    /**
     * Define methods for the manipulation of resources.
     */
    public enum CommandMethod {
        /**
         * Gets an existing value of the resource.
         * */
        get,
        /**
         * Sets or updates a for the resource. 
         */
        set,
        /**
         *  Deletes a value of the resource or the resource itself.
         */
        delete,
        /**
         *  Notify the destination about a change in the resource value of the sender.
         *  This method is one way and the destination SHOULD NOT send a response for it.
         *  Because of that, a command envelope with this method MAY NOT have an id.
         */
        observe
    }

    /**
     * Represents the status of a resource operation.
     */
    public enum CommandStatus {
        /**
         *  The resource action is pending.
         */
        pending,
        /**
         *  The resource action was successfully executed.
         */
        success,
        /**
         * The resource action has FAILED.
         */
        failure
    }
}
