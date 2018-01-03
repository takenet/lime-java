package net.take.iris.messaging.resources;

import org.limeprotocol.*;

import java.util.Date;
import java.util.Map;

public class ThreadMessage extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.iris.thread-message+json";

    public ThreadMessage(){
        super(MediaType.parse(MIME_TYPE));
    }

    private String id;
    private Identity peerIdentity;
    private ThreadMessageDirection direction;
    private MediaType type;
    private Document content;
    private Date date;
    private Notification.Event status;
    private Reason reason;
    private Map<String, String> metadata;

    /**
     * Gets the message id
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the message id
     * @return
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the peer identity. If is an attendace message, peerIdentity is a service identity otherwise is the other peer on conversation.
     * @return
     */
    public Identity getPeerIdentity() {
        return peerIdentity;
    }

    /**
     * Sets the peer identity. If is an attendace message, peerIdentity is a service identity otherwise is the other peer on conversation.
     * @param peerIdentity
     */
    public void setPeerIdentity(Identity peerIdentity) {
        this.peerIdentity = peerIdentity;
    }

    /**
     * Get the direction of the message in the thread.
     * @return
     */
    public ThreadMessageDirection getDirection() {
        return direction;
    }

    /**
     * Set the direction of the message in the thread.
     * @param direction
     */
    public void setDirection(ThreadMessageDirection direction) {
        this.direction = direction;
    }

    /**
     * Gets the media type of the @content value.
     * @return
     */
    public MediaType getType() {
        return type;
    }

    /**
     * Sets the media type of the @content value.
     * @param type
     */
    public void setType(MediaType type) {
        this.type = type;
    }

    /**
     * Gets the last thread message content.
     * @return
     */
    public Document getContent() {
        return content;
    }

    /**
     * Sets the last thread message content.
     * @param content
     */
    public void setContent(Document content) {
        this.content = content;
    }

    /**
     * Gets the message date.
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the message date.
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the status, if the direction value is @threadMessageDirection.SENT.
     * @return
     */
    public Notification.Event getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status
     */
    public void setStatus(Notification.Event status) {
        this.status = status;
    }

    /**
     * Gets the failure reason, in case of @status value is @Event.FAILED.
     * @return
     */
    public Reason getReason() {
        return reason;
    }

    /**
     * Sets the failure reason.
     * @param reason
     */
    public void setReason(Reason reason) {
        this.reason = reason;
    }

    /**
     * Gets the metadata of the original message
     * @return
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata of the original message
     * @param metadata
     */
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
