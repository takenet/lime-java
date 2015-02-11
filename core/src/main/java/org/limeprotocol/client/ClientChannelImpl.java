package org.limeprotocol.client;

import org.limeprotocol.*;
import org.limeprotocol.network.ChannelBase;
import org.limeprotocol.network.Transport;
import org.limeprotocol.security.Authentication;

import java.io.IOException;
import java.util.UUID;

public class ClientChannelImpl extends ChannelBase implements ClientChannel {
    public ClientChannelImpl(Transport transport) {
        this(transport, false);
    }
    public ClientChannelImpl(Transport transport, boolean fillEnvelopeRecipients) {
        this(transport, fillEnvelopeRecipients, false);
    }
    public ClientChannelImpl(Transport transport, boolean fillEnvelopeRecipients, boolean autoReplyPings) {
        super(transport, fillEnvelopeRecipients, autoReplyPings);
    }

    /**
     * Sends a new session envelope to the server and listen for the response.
     *
     * @param sessionListener
     */
    @Override
    public void startNewSession(SessionChannelListener sessionListener) throws IOException {
        if (getState() != Session.SessionState.NEW) {
            throw new IllegalStateException(String.format("Cannot start a session in the '%s' state.", getState()));
        }
        setSessionListener(sessionListener);
        Session session = new Session();
        session.setState(Session.SessionState.NEW);
        sendSession(session);
    }

    /**
     * Sends a negotiate session envelope to accepts the session negotiation options
     * and listen for the server confirmation.
     *
     * @param sessionCompression
     * @param sessionEncryption
     * @param sessionListener
     */
    @Override
    public void negotiateSession(SessionCompression sessionCompression, SessionEncryption sessionEncryption, SessionChannelListener sessionListener) throws IOException {
        if (getState() != Session.SessionState.NEGOTIATING) {
            throw new IllegalStateException(String.format("Cannot negotiate a session in the '%s' state.", getState()));
        }
        setSessionListener(sessionListener);
        Session session = new Session();
        session.setId(super.getSessionId());
        session.setState(Session.SessionState.NEGOTIATING);
        session.setCompression(sessionCompression);
        session.setEncryption(sessionEncryption);
        sendSession(session);
    }

    /**
     * Sends a authenticate session envelope to the server to establish an authenticated session
     * and listen for the established session envelope.
     *
     * @param identity
     * @param authentication
     * @param instance
     * @param sessionListener
     */
    @Override
    public void authenticateSession(Identity identity, Authentication authentication, String instance, SessionChannelListener sessionListener) throws IOException {
        if (super.getState() != Session.SessionState.AUTHENTICATING) {
            throw new UnsupportedOperationException(String.format("Cannot authenticate a session in the '%s' state", getState()));
        }
        if (identity == null) {
            throw new IllegalArgumentException("identity");
        }
        if (authentication == null) {
            throw new IllegalArgumentException("authentication");
        }
        setSessionListener(sessionListener);
        Session session = new Session();
        session.setId(getSessionId());
        session.setFrom(new Node(identity.getName(), identity.getDomain(), instance));
        session.setState(Session.SessionState.AUTHENTICATING);
        session.setAuthentication(authentication);
        sendSession(session);
    }

    /**
     * Notify to the server that the specified message was received by the peer.
     *
     * @param messageId
     * @param to
     */
    @Override
    public void sendReceivedNotification(UUID messageId, Node to) {

    }

    /**
     * Sends a finishing session envelope to the server.
     */
    @Override
    public void sendFinishingSession() {

    }

    /**
     * Listens for a finished session envelope from the server.
     *
     * @param sessionListener
     */
    @Override
    public void receiveFinishedSession(SessionChannelListener sessionListener) {

    }


    @Override
    protected synchronized void raiseOnReceiveSession(Session session) {
        super.raiseOnReceiveSession(session);
        setSessionId(session.getId());
        setState(session.getState());
        
        if (session.getState() == Session.SessionState.ESTABLISHED) {
            setLocalNode(session.getTo());
            setRemoteNode(session.getFrom());
        } else if (session.getState() == Session.SessionState.FINISHED || 
                session.getState() == Session.SessionState.FAILED) {
            try {
                getTransport().close();
            } catch (Exception e) {
                transportListenerException = e;
            }
        }
    }
}
