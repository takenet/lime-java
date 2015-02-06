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
     * @param messageChannelListener
     */
    void setMessageChannelListener(MessageChannelListener messageChannelListener);

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
