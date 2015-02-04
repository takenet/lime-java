package org.limeprotocol.messaging.contents;

import org.limeprotocol.Document;
import org.limeprotocol.MediaType;

/// <summary>
/// Allows the chat clients to exchange
/// information about conversation events.
/// </summary>
public class ChatState implements Document {
    public final String MIME_TYPE = "application/vnd.lime.chatstate+json";

    public final String STATE_KEY = "state";
    private MediaType mediaType;

    public ChatState() {
        this.mediaType = MediaType.parse(MIME_TYPE);
    }

    private ChatStateEvent state;

    @Override
    public String toString() {
        return this.state.toString();
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }


    /**
     * The current chat state.
     */
    public enum ChatStateEvent {
        /**
         * The other chat party started
         * a new chat a conversation.
         */
        Starting,
        /**
         * The other party is typing.
         */
        Composing,
        /**
         * The other party was
         * typing but stopped.
         */
        Paused,
        /**
         * The other party is
         * deleting a text.
         */
        Deleting,
        /**
         * The other party
         * left the conversation.
         */
        Gone
    }
}
