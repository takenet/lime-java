package org.limeprotocol.client;

import org.limeprotocol.*;
import org.limeprotocol.network.Channel;
import org.limeprotocol.security.Authentication;

import java.io.IOException;
import java.util.UUID;

/**
 * Defines the communication channel between a client node and a server.
 */
public interface ClientChannel extends Channel {

    /**
     * Sends a new session envelope to the server and listen for the response.
     * @param sessionListener
     */
    void startNewSession(SessionChannelListener sessionListener) throws IOException;

    /**
     * Sends a negotiate session envelope to accepts the session negotiation options 
     * and listen for the server confirmation.
     * @param sessionCompression
     * @param sessionEncryption
     * @param sessionListener
     */
    void negotiateSession(SessionCompression sessionCompression, SessionEncryption sessionEncryption, SessionChannelListener sessionListener) throws IOException;

    /**
     * Sends a authenticate session envelope to the server to establish an authenticated session 
     * and listen for the established session envelope.
     * @param identity
     * @param authentication
     * @param instance
     * @param sessionListener
     */
    void authenticateSession(Identity identity, Authentication authentication, String instance, SessionChannelListener sessionListener) throws IOException;

    /**
     *  Notify to the server that the specified message was received by the peer.
     * @param messageId
     * @param to
     */
    void sendReceivedNotification(UUID messageId, Node to) throws IOException;

    /**
     * Sends a finishing session envelope to the server.
     */
    void sendFinishingSession() throws IOException;

    /**
     * Performs the session negotiation and authentication
     * @param compression Chosen compression, or null for the first one supported by the server
     * @param encryption Chosen encryption, or null for the first one supported by the server
     * @param identity
     * @param authentication
     */
    void establishSession(SessionCompression compression, SessionEncryption encryption,
                                  Identity identity, Authentication authentication, String instance,
                                  EstablishSessionListener listener)
            throws IOException;

    /**
     * Defines listener for session establishment
     */
    public interface EstablishSessionListener extends SessionChannelListener {
        /**
         * Occurs if there is any unexpected failure during session establishment
         * @param exception
         */
        void onFailure(Exception exception);
    }
}