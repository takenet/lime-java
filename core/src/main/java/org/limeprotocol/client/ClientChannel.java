package org.limeprotocol.client;

import org.limeprotocol.Identity;
import org.limeprotocol.Node;
import org.limeprotocol.SessionCompression;
import org.limeprotocol.SessionEncryption;
import org.limeprotocol.network.Channel;
import org.limeprotocol.network.SessionChannel;
import org.limeprotocol.security.Authentication;

import java.util.UUID;

/**
 * Defines the communication channel between a client node and a server.
 */
public interface ClientChannel extends Channel {

    /**
     * Sends a new session envelope to the server and listen for the response.
     * @param sessionListener
     * @param channelListener
     */
    void startNewSession(SessionChannelListener sessionListener, ChannelListener channelListener);

    /**
     * Sends a negotiate session envelope to accepts the session negotiation options 
     * and listen for the server confirmation.
     * @param sessionCompression
     * @param sessionEncryption
     * @param sessionListener
     * @param channelListener
     */
    void negotiateSession(SessionCompression sessionCompression, SessionEncryption sessionEncryption, SessionChannelListener sessionListener, ChannelListener channelListener);

    /**
     *  Listens for a authenticating session envelope from the server, after a session negotiation.
     * @param sessionListener
     * @param channelListener
     */
    void receiveAuthenticationSession(SessionChannelListener sessionListener, ChannelListener channelListener);

    /**
     * Sends a authenticate session envelope to the server to establish an authenticated session 
     * and listen for the established session envelope.
     * @param identity
     * @param authentication
     * @param instance
     * @param sessionListener
     * @param channelListener
     */
    void authenticateSession(Identity identity, Authentication authentication, String instance, SessionChannelListener sessionListener, ChannelListener channelListener);

    /**
     *  Notify to the server that the specified message was received by the peer.
     * @param messageId
     * @param to
     */
    void sendReceivedNotification(UUID messageId, Node to);

    /**
     * Sends a finishing session envelope to the server.
     */
    void sendFinishingSession();

    /**
     *  Listens for a finished session envelope from the server.
     * @param sessionListener
     * @param channelListener
     */
    void receiveFinishedSession(SessionChannelListener sessionListener, ChannelListener channelListener);
}