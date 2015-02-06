package org.limeprotocol.network;

import org.limeprotocol.Node;
import org.limeprotocol.Session;

import java.util.UUID;

/**
 * Defines a communication channel for the protocol.
 */
public interface Channel extends MessageChannel, CommandChannel, NotificationChannel, SessionChannel {
    /**
     * Gets the current session transport
     * @return
     */
    Transport getTransport();

    /**
     * Gets the remote node identifier.
     * @return
     */
    Node getRemoteNode();

    /**
     * Gets the local node identifier.
     * @return
     */
    Node getLocalNode();

    /**
     * Gets the current session Id.
     * @return
     */
    UUID getSessionId();

    /**
     * Gets the current session state.
     * @return
     */
    Session.SessionState getState();

    /**
     * Sets the channel listener.
     * @param channelListener
     */
    void setChannelListener(ChannelListener channelListener);
    
    /**
     * Defines a listener for channel events. 
     */
    public interface ChannelListener {
        /**
         * Occurs when the transport listener has thrown an exception.
         * @param exception
         */
        void onTransportException(Exception exception);

        /**
         * Occurs when the transport is about to be closed.
         */
        void onTransportClosing();

        /**
         * Occurs after the transport was closed.
         */
        void onTransportClosed();
    }
}
