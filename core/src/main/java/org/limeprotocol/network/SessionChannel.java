package org.limeprotocol.network;

import org.limeprotocol.Session;

import java.io.IOException;

/**
 * Defines a session envelopes exchanging channel.
 */
public interface SessionChannel {
    /**
     * Sends a session to the remote node.
     * @param session
     */
    void sendSession(Session session) throws IOException;

    /**
     * Sets the listener for receiving sessions.
     * @param sessionChannelListener
     */
    void setSessionChannelListener(SessionChannelListener sessionChannelListener);

    /**
     * Defines a session channel listener.
     */
    public interface SessionChannelListener {
        /**
         * Occurs when a session is received by the channel.
         * @param session
         */
        void onReceiveSession(Session session);
    }
}
