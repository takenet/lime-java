package org.limeprotocol.messaging;

/**
 * Represents the message events
 * that should generate receipts
 * (notifications) for the node in the
 * current session.
 */
import org.limeprotocol.Document;
import org.limeprotocol.MediaType;
import org.limeprotocol.Notification.Event;

public class Receipt implements Document {
    public final String MIME_TYPE = "application/vnd.lime.receipt+json";

    public final String EVENTS_KEY = "events";

    private MediaType mediaType;

    public Receipt()
    {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    /**
     * Indicates which message events
     * that the node is receiving
     * in the current session.
     */
    public Event[] events;

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }

    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }
}