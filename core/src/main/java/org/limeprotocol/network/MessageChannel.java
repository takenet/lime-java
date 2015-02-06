package org.limeprotocol.network;

import org.limeprotocol.Message;

import java.io.IOException;

/**
 * Defines a message envelopes exchanging channel.
 */
public interface MessageChannel {
    /**
     * Sends a message to the remote node.
     * @param message
     */
    void sendMessage(Message message) throws IOException;

    /**
     * Sets the listener for receiving messages.
     * @param listener
     * @param singleReceive
     */
    void addMessageListener(MessageChannelListener listener, boolean singleReceive);

    /**
     * Removes the specified listener.
     * @param listener
     */
    void removeMessageListener(MessageChannelListener listener);

    /**
     * Defines a message channel listener.
     */
    public interface MessageChannelListener {
        /**
         * Occurs when a message is received by the channel.
         * @param message
         */
        void onReceiveMessage(Message message);
    }
}
