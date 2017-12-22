package ai.blip.messaging.resources;

/**
 * Indicates the direction of a message in a thread.
 */
public enum ThreadMessageDirection {

    /**
     * The message was sent by the thread owner.
     */
    SENT,
    /**
     * The message was received by the thread owner.
     */
    RECEIVED
}
