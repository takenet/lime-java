package org.limeprotocol;

/**
 * Transports information about events associated to a message
 * sent in a session. Can be originated by a server
 * or by the message destination node.
 */
public class Notification extends Envelope {

    public Notification() {
        //Create a Envelope with a Empty Id
        super();
    }

    public Notification(String id) {
        super(id);
    }

    /**
     * Related event to the notification
     */
    public Event event;

    /**
     * In the case of a failed event,
     * brings more details about
     * the problem.
     */
    public Reason reason;

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
     * Events that can happen in the message pipeline
     */
    public enum Event {
        /**
         * A problem occurred during the processing
         * of the message. In this case, the reason
         * property of the notification SHOULD be
         * present.
         */
        FAILED,

        /**
         * The message was received
         * and accepted by the server.
         */
        ACCEPTED,

        /**
         * The message format was
         * validated by the server.
         */

        VALIDATED,

        /**
         * The dispatch of the message
         * was authorized by the server.
         */

        AUTHORIZED,

        /**
         * The message was dispatched to
         * the destination by the server.
         */

        DISPATCHED,

        /**
         * The destination has
         * received the message.
         */

        RECEIVED,

        /**
         * The destination has
         * consumed the content of
         * the message.
         */
        CONSUMED
    }

}
