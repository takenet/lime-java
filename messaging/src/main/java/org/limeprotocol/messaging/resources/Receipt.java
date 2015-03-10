package org.limeprotocol.messaging.resources;

/**
 * Represents the message events
 * that should generate receipts
 * (notifications) for the node in the
 * current session.
 */
import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;
import org.limeprotocol.Notification.Event;

public class Receipt extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.lime.receipt+json";

    /**
     * Indicates which message events
     * that the node is receiving
     * in the current session.
     */
    private Event[] events;

    public Receipt()
    {
        super(MediaType.parse(MIME_TYPE));
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}