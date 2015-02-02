package org.limeprotocol;

import java.util.UUID;

public class Command extends Envelope {
    public final String URI_KEY = "uri";
    public final String TYPE_KEY = Message.TYPE_KEY;
    public final String RESOURCE_KEY = "resource";
    public final String METHOD_KEY = "method";
    public final String STATUS_KEY = "status";
    public final String REASON_KEY = "reason";

    public Command() {
    }

    public Command(UUID id) {
        super(id);
    }

    /**
     * The universal identifier
     * of the resource
     */
    public LimeUri uri;

    /**
     *   MIME declaration of the resource type of the command.
     */
    public MediaType type;

    /**
     *  Server resource that are subject
     *  of the command
     */
    public Document resource;

    /**
     *  Action to be taken to the
     *  resource
     */
    public CommandMethod method;

    /**
     *  Indicates the status of
     *  the action taken to the resource
     */
    public CommandStatus status;

    /**
     *  Indicates a reason for
     *  the status
     */
    public Reason reason;


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

    /// <summary>
    /// Defines method for the manipulation
    /// of messaging.
    /// </summary>
    public enum CommandMethod {
        /// <summary>
        /// Gets an existing value of the resource.
        /// </summary>
        Get,
        /// <summary>
        /// Sets or updates a for the resource.
        /// </summary>
        Set,
        /// <summary>
        /// Deletes a value of the resource
        /// or the resource itself.
        /// </summary>
        Delete,
        /// <summary>
        /// Notify the destination about a change
        /// in the resource value of the sender.
        /// This method is one way and the destination
        /// SHOULD NOT send a response for it.
        /// Because of that, a command envelope with this
        /// method MAY NOT have an id.
        /// </summary>
        Observe
    }

    /// <summary>
    /// Represents the status
    /// of a resource operation
    /// </summary>
    public enum CommandStatus {
        /// <summary>
        /// The resource action is pending
        /// </summary>
        Pending,
        /// <summary>
        /// The resource action was
        /// sucessfully
        /// </summary>
        Success,
        /// <summary>
        ///
        /// </summary>
        Failure
    }


}
