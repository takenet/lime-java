package org.limeprotocol;

/**
 * Transports information about events associated to a message
 * sent in a session. Can be originated by a server
 * or by the message destination node.
 */
public class Notification extends Envelope {

    public Notification() {
        //Create a Envelope with a Empty Id
        super(null);
    }

    /**
     * Related event to the notification
     */
    private Event event;

    /**
     * In the case of a failed event,
     * brings more details about
     * the problem.
     */
    private Reason reason;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    /**
     * Events that can happen
     * in the message pipeline
     */
    public enum Event {
        /**
         * A problem occurred during the processing
         * of the message. In this case, the reason
         * property of the notification SHOULD be
         * present.
         */
        Failed,

        /**
         * The message was received
         * and accepted by the server.
         */
        Accepted,

        /**
         * The message format was
         * validated by the server.
         */
        Validated,

        /**
         * The dispatch of the message
         * was authorized by the server.
         */
        Authorized,

        /**
         * The message was dispatched to
         * the destination by the server.
         */
        Dispatched,

        /**
         * The destination has
         * received the message.
         */
        Received,

        /**
         * The destination has
         * consumed the content of
         * the message.
         */
        Consumed
    }

}
