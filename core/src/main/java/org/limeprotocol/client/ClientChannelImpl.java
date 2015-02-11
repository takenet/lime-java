package org.limeprotocol.client;

import org.limeprotocol.*;
import org.limeprotocol.network.ChannelBase;
import org.limeprotocol.network.SessionChannel;
import org.limeprotocol.network.Transport;
import org.limeprotocol.security.Authentication;
import org.limeprotocol.util.StringUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.limeprotocol.Session.SessionState.*;

public class ClientChannelImpl extends ChannelBase implements ClientChannel {

    private boolean autoNotifyReceipt;


    public ClientChannelImpl(Transport transport) {
        this(transport, false);
    }
    
    public ClientChannelImpl(Transport transport, boolean fillEnvelopeRecipients) {
        this(transport, fillEnvelopeRecipients, false);
    }
    
    public ClientChannelImpl(Transport transport, boolean fillEnvelopeRecipients, boolean autoReplyPings) {
        this(transport, fillEnvelopeRecipients, autoReplyPings, false);
    }

    public ClientChannelImpl(Transport transport, boolean fillEnvelopeRecipients, boolean autoReplyPings, 
                             boolean autoNotifyReceipt) {
        super(transport, fillEnvelopeRecipients, autoReplyPings);
        
        this.autoNotifyReceipt = autoNotifyReceipt;
    }

    /**
     * Sends a new session envelope to the server and listen for the response.
     *
     * @param sessionListener
     */
    @Override
    public void startNewSession(SessionChannelListener sessionListener) throws IOException {
        if (getState() != NEW) {
            throw new IllegalStateException(String.format("Cannot start a session in the '%s' state.", getState()));
        }
        setSessionListener(sessionListener);
        Session session = new Session();
        session.setState(NEW);
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
        if (getState() != NEGOTIATING) {
            throw new IllegalStateException(String.format("Cannot negotiate a session in the '%s' state.", getState()));
        }
        setSessionListener(sessionListener);
        Session session = new Session();
        session.setId(super.getSessionId());
        session.setState(NEGOTIATING);
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
        if (getState() != AUTHENTICATING) {
            throw new IllegalStateException(String.format("Cannot authenticate a session in the '%s' state", getState()));
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
        session.setState(AUTHENTICATING);
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
    public void sendReceivedNotification(final UUID messageId, final Node to) throws IOException {
        if (to == null) {
            throw new IllegalArgumentException("to");
        }

        Notification notification = new Notification() {{
            setId(messageId);
            setTo(to);
            setEvent(Event.RECEIVED);
        }};

        sendNotification(notification);
    }

    /**
     * Sends a finishing session envelope to the server.
     */
    @Override
    public void sendFinishingSession() throws IOException {
        if (getState() != ESTABLISHED) {
            throw new IllegalStateException(String.format("Cannot finish a session in the '%s' state", getState()));
        }

        Session session = new Session() {{
            setId(getSessionId());
            setState(FINISHING);
        }};

        sendSession(session);
    }

    @Override
    public Session establishSession(SessionCompression compression, SessionEncryption encryption,
                                    Identity identity, Authentication authentication, String instance)
            throws IOException, InterruptedException, TimeoutException {

        if (getState() != NEW) {
            throw new IllegalStateException(String.format("Cannot establish a session in the '%s' state", getState()));
        }

        if (identity == null) {
            throw new IllegalArgumentException("identity");
        }
        if (authentication == null) {
            throw new IllegalArgumentException("authentication");
        }

        Session receivedSession = null;
        SessionReceiver receiver = new SessionReceiver();

        this.startNewSession(receiver);
        receivedSession = receiver.waitForResponse();

        if (receivedSession.getState() == NEGOTIATING) {
            SessionCompression desiredCompression = compression;
            if (desiredCompression == null) {
                desiredCompression = receivedSession.getCompressionOptions()[0];
            }

            SessionEncryption desiredEncryption = encryption;
            if (desiredEncryption == null) {
                desiredEncryption = receivedSession.getEncryptionOptions()[0];
            }

            this.negotiateSession(desiredCompression, desiredEncryption, receiver);
            receivedSession = receiver.waitForResponse();

            if (receivedSession.getState() == NEGOTIATING) {
                // Configure transport
                if (receivedSession.getCompression() != this.getTransport().getCompression()) {
                    this.getTransport().setCompression(receivedSession.getCompression());
                }
                if (receivedSession.getEncryption() != this.getTransport().getEncryption()) {
                    this.getTransport().setEncryption(receivedSession.getEncryption());
                }
                this.setSessionListener(receiver);
                receivedSession = receiver.waitForResponse();
            }

            if (receivedSession.getState() == AUTHENTICATING) {
                do {
                    this.authenticateSession(identity, authentication, instance, receiver);
                    receivedSession = receiver.waitForResponse();
                } while (receivedSession.getState() == AUTHENTICATING);
            }
        }

        return receivedSession;
    }
    /**
     *  Fills the envelope recipients
     *  using the session information
     */
    @Override
    protected void fillEnvelope(Envelope envelope, boolean isSending)
    {
        super.fillEnvelope(envelope, isSending);

        if (isSending &&
                this.getLocalNode() != null)
        {
            if (envelope.getPp() == null)
            {
                if (envelope.getFrom() != null &&
                        !envelope.getFrom().equals(this.getLocalNode()))
                {
                    envelope.setPp(this.getLocalNode().copy());
                }
            } else if (StringUtils.isNullOrWhiteSpace(envelope.getPp().getDomain()))
            {
                envelope.getPp().setDomain(this.getLocalNode().getDomain());
            }
        }
    }

    @Override
    protected synchronized void raiseOnReceiveSession(Session session) {
        super.raiseOnReceiveSession(session);
        setSessionId(session.getId());
        setState(session.getState());
        
        if (session.getState() == ESTABLISHED) {
            setLocalNode(session.getTo());
            setRemoteNode(session.getFrom());
        } else if (session.getState() == FINISHED ||
                session.getState() == FAILED) {
            try {
                getTransport().close();
            } catch (Exception e) {
                transportListenerException = e;
            }
        }
    }

    @Override
    protected synchronized void raiseOnReceiveMessage(Message message) {
        super.raiseOnReceiveMessage(message);

        if(autoNotifyReceipt &&
                message.getId() != null &&
                message.getFrom() != null){

            Notification notification = new Notification();
            notification.setId(message.getId());
            notification.setTo(message.getFrom());
            notification.setEvent(Notification.Event.RECEIVED);

            try {
                sendNotification(notification);
            } catch (IOException e) {
                transportListenerException = e;
            }
        }
    }

    private class SessionReceiver implements SessionChannelListener {
        private final int RECEIVE_TIMEOUT_IN_SECS = 2;

        private final Semaphore semaphore = new Semaphore(1);
        private final Session[] receivedSession = { null };

        public SessionReceiver() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onReceiveSession(Session session) {
            receivedSession[0] = session;
            semaphore.release();
        }

        public Session waitForResponse() throws InterruptedException, TimeoutException {
            if (semaphore.tryAcquire(1, RECEIVE_TIMEOUT_IN_SECS, TimeUnit.SECONDS) &&
                    receivedSession[0] != null) {
                return receivedSession[0];
            } else {
                throw new TimeoutException();
            }
        }
    }
}
