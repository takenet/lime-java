package org.limeprotocol.client;

import org.limeprotocol.*;
import org.limeprotocol.network.ChannelBase;
import org.limeprotocol.network.Transport;
import org.limeprotocol.security.Authentication;
import org.limeprotocol.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

public class ClientChannelImpl extends ChannelBase implements ClientChannel {
    protected ClientChannelImpl(Transport transport, boolean fillEnvelopeRecipients) {
        super(transport, fillEnvelopeRecipients);
    }

    /**
     * Sends a new session envelope to the server and listen for the response.
     *
     * @param sessionListener
     * @param channelListener
     */
    @Override
    public void startNewSession(SessionChannelListener sessionListener, ChannelListener channelListener) throws IOException {

        if(super.getState() != Session.SessionState.NEW){
            throw new UnsupportedOperationException(StringUtils.format("Cannot start a session in the '{0}' state", super.getState()));
        }

        super.addSessionListener(sessionListener, true);
        super.addChannelListener(channelListener, true);

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
     * @param channelListener
     */
    @Override
    public void negotiateSession(SessionCompression sessionCompression, SessionEncryption sessionEncryption, SessionChannelListener sessionListener, ChannelListener channelListener) throws IOException {

        if (super.getState() != Session.SessionState.NEGOTIATING)
        {
            throw new UnsupportedOperationException(StringUtils.format("Cannot negotiate a session in the '{0}' state", super.getState()));
        }

        Session session = new Session();
        session.setId(super.getSessionId());
        session.setState(Session.SessionState.NEGOTIATING);
        session.setCompression(sessionCompression);
        session.setEncryption(sessionEncryption);

        super.addSessionListener(sessionListener, true);
        super.addChannelListener(channelListener, true);

        sendSession(session);
    }

    /**
     * Listens for a authenticating session envelope from the server, after a session negotiation.
     *
     * @param sessionListener
     * @param channelListener
     */
    @Override
    public void receiveAuthenticationSession(SessionChannelListener sessionListener, ChannelListener channelListener) {

        if (super.getState() != Session.SessionState.NEGOTIATING)
        {
            throw new UnsupportedOperationException(StringUtils.format("Cannot receive a authenticating session in the '{0}' state", super.getState()));
        }

        super.addSessionListener(sessionListener, true);
        super.addChannelListener(channelListener, true);
    }

    /**
     * Sends a authenticate session envelope to the server to establish an authenticated session
     * and listen for the established session envelope.
     *
     * @param identity
     * @param authentication
     * @param instance
     * @param sessionListener
     * @param channelListener
     */
    @Override
    public void authenticateSession(Identity identity, Authentication authentication, String instance, SessionChannelListener sessionListener, ChannelListener channelListener) throws IOException {

        if (super.getState() != Session.SessionState.AUTHENTICATING)
        {
            throw new UnsupportedOperationException(StringUtils.format("Cannot authenticate a session in the '{0}' state", super.getState()));
        }

        if (identity == null)
        {
            throw new IllegalArgumentException("identity");
        }

        if (authentication == null)
        {
            throw new IllegalArgumentException("authentication");
        }

        super.addSessionListener(sessionListener, true);
        super.addChannelListener(channelListener, true);

        Session session = new Session();
        session.setId(super.getSessionId());
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
     * @param channelListener
     */
    @Override
    public void receiveFinishedSession(SessionChannelListener sessionListener, ChannelListener channelListener) {

    }
}
