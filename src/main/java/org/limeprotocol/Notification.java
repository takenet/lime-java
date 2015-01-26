package org.limeprotocol;

import org.limeprotocol.util.UUIDUtils;

/// <summary>
/// Transports information about events associated to a message
/// sent in a session. Can be originated by a server
/// or by the message destination node.
/// </summary>
public class Notification extends Envelope {


    public final String EVENT_KEY = "event";
    public final String REASON_KEY = "reason";

    public Notification() {
        //Create a Envelope with a Empty Id
        super(UUIDUtils.empty());
    }

    /// <summary>
    /// Related event to the notification
    /// </summary>
    public Event event;

    /// <summary>
    /// In the case of a failed event,
    /// brings more details about
    /// the problem.
    /// </summary>
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

    /// <summary>
    /// Events that can happen
    /// in the message pipeline
    /// </summary>
    public enum Event {
        /// <summary>
        /// A problem occurred during the processing
        /// of the message. In this case, the reason
        /// property of the notification SHOULD be
        /// present.
        /// </summary>
        Failed,

        /// <summary>
        /// The message was received
        /// and accepted by the server.
        /// </summary>
        Accepted,

        /// <summary>
        /// The message format was
        /// validated by the server.
        /// </summary>

        Validated,

        /// <summary>
        /// The dispatch of the message
        /// was authorized by the server.
        /// </summary>

        Authorized,

        /// <summary>
        /// The message was dispatched to
        /// the destination by the server.
        /// </summary>

        Dispatched,

        /// <summary>
        /// The destination has
        /// received the message.
        /// </summary>

        Received,

        /// <summary>
        /// The destination has
        /// consumed the content of
        /// the message.
        /// </summary>
        Consumed
    }

}
