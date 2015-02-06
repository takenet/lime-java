package org.limeprotocol.messaging.contents;

import org.limeprotocol.Document;
import org.limeprotocol.DocumentBase;
import org.limeprotocol.MediaType;

/**
 * Allows the chat clients to exchange
 * information about conversation events.
 */
public class ChatState extends DocumentBase {
    public static final String MIME_TYPE = "application/vnd.lime.chatstate+json";

    private ChatStateEvent state;

    public ChatState() {
        super(MediaType.parse(MIME_TYPE));
    }

    public ChatStateEvent getState() {
        return state;
    }

    public void setState(ChatStateEvent state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return this.state.toString();
    }

    /**
     * The current chat state.
     */
    public enum ChatStateEvent {
        /**
         * The other chat party started
         * a new chat a conversation.
         */
        STARTING,

        /**
         * The other party is typing.
         */
        COMPOSING,

        /**
         * The other party was
         * typing but stopped.
         */
        PAUSED,

        /**
         * The other party is
         * deleting a text.
         */
        DELETING,

        /**
         * The other party
         * left the conversation.
         */
        GONE
    }
}
