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
     * Register a channel listener.
     * @param channelListener
     */
    void addChannelListener(ChannelListener channelListener, boolean removeOnException);

    /**
     * Removes a registered channel listener.
     * * @param channelListener
     */
    void removeChannelListener(ChannelListener channelListener);
    
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
