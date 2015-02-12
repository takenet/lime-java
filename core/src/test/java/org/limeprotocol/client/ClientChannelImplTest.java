package org.limeprotocol.client;

import org.junit.Before;
import org.junit.Test;
import org.limeprotocol.*;
import org.limeprotocol.security.Authentication;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.testHelpers.TestClientChannel;
import org.limeprotocol.testHelpers.TestTransport;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.limeprotocol.Session.SessionState;
import static org.limeprotocol.network.SessionChannel.SessionChannelListener;
import static org.limeprotocol.testHelpers.Dummy.*;
import static org.mockito.Mockito.*;

public class ClientChannelImplTest {

    private TestTransport transport;
    @Mock private SessionChannelListener listener;
    @Captor private ArgumentCaptor<Session> sessionArgument;

    @Before
    public void setUp() throws Exception {
        transport = new TestTransport();
        MockitoAnnotations.initMocks(this);
    }

    //region startNewSession

    @Test
    public void startNewSession_NewState_CallsTransportAndReadsFromBuffer() throws Exception {
        // Arrange
        Session authenticationSession = createSession(SessionState.AUTHENTICATING);
        transport.addNextEnvelopeToReturn(authenticationSession);

        TestClientChannel target = getTarget();

        // Act
        target.startNewSession(listener);

        // Assert

        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Session.class);
        Session sentSession = (Session)transport.getSentEnvelopes()[0];
        assertThat(sentSession.getState()).isEqualTo(Session.SessionState.NEW);

        verify(listener).onReceiveSession(sessionArgument.capture());
        Session sessionReturned = sessionArgument.getValue();
        assertThat(sessionReturned).isNotNull().isEqualTo(authenticationSession);
    }

    @Test
    public void startNewSession_InvalidState_ThrowsIllegalStateException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.ESTABLISHED);

        // Act
        try {
            target.startNewSession(listener);
        } catch (IllegalStateException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An IllegalStateException should be threw");
    }

    //endregion startNewSession

    //region negotiateSession

    @Test
    public void negotiateSession_NegotiatingState_CallsTransportAndReadsFromBuffer() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.NEGOTIATING);

        SessionCompression compression = SessionCompression.GZIP;
        SessionEncryption encryption = SessionEncryption.TLS;

        Session negotiatingSession = createSession(SessionState.NEGOTIATING);
        negotiatingSession.setId(target.getSessionId());

        transport.addNextEnvelopeToReturn(negotiatingSession);

        // Act
        target.negotiateSession(compression, encryption, listener);

        // Assert
        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Session.class);
        Session sentSession = (Session)transport.getSentEnvelopes()[0];
        assertThat(sentSession.getState()).isEqualTo(SessionState.NEGOTIATING);
        assertThat(sentSession.getCompression()).isEqualTo(compression);
        assertThat(sentSession.getEncryption()).isEqualTo(encryption);

        verify(listener).onReceiveSession(sessionArgument.capture());
        Session sessionReturned = sessionArgument.getValue();
        assertThat(sessionReturned).isNotNull().isEqualTo(negotiatingSession);
    }

    @Test
    public void negotiateSession_InvalidState_ThrowsIllegalStateExceptionException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.NEW);

        SessionCompression compression = SessionCompression.GZIP;
        SessionEncryption encryption = SessionEncryption.TLS;

        // Act
        try {
            target.negotiateSession(compression, encryption, listener);
        } catch (IllegalStateException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An UnsupportedOperationException should be threw");
    }
    //endregion negotiateSession

    //region authenticateSession

    @Test
    public void authenticateSessionAuthenticatingState_CallsTransportAndReadsFromTransport() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.AUTHENTICATING);

        Identity localIdentity = createIdentity();
        String localInstance = createInstanceName();
        Authentication plainAuthentication = createPlainAuthentication();

        Session establishedSession = createSession(SessionState.ESTABLISHED);
        establishedSession.setId(target.getSessionId());

        transport.addNextEnvelopeToReturn(establishedSession);

        // Act
        target.authenticateSession(localIdentity, plainAuthentication, localInstance, listener);

        // Assert
        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Session.class);
        Session sentSession = (Session)transport.getSentEnvelopes()[0];
        assertThat(sentSession.getState()).isEqualTo(SessionState.AUTHENTICATING);
        assertThat(sentSession.getFrom().toIdentity()).isEqualTo(localIdentity);
        assertThat(sentSession.getFrom().getInstance()).isEqualTo(localInstance);
        assertThat(sentSession.getAuthentication()).isEqualTo(plainAuthentication);

        verify(listener).onReceiveSession(sessionArgument.capture());
        Session sessionReturned = sessionArgument.getValue();
        assertThat(sessionReturned).isNotNull().isEqualTo(establishedSession);
    }

    @Test
    public void authenticateSessionInvalidState_ThrowsIllegalStateException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.ESTABLISHED);

        Identity localIdentity = createIdentity();
        String localInstance = createInstanceName();
        Authentication plainAuthentication = createPlainAuthentication();

        // Act
        try {
            target.authenticateSession(localIdentity, plainAuthentication, localInstance, listener);
        } catch (IllegalStateException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An IllegalStateException should be threw");
    }

    @Test
    public void authenticateSessionNullIdentity_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.AUTHENTICATING);

        Identity localIdentity = null;
        String localInstance = createInstanceName();
        Authentication plainAuthentication = createPlainAuthentication();

        // Act
        try {
            target.authenticateSession(localIdentity, plainAuthentication, localInstance, listener);
        } catch (IllegalArgumentException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An IllegalArgumentException should be threw");
    }

    @Test
    public void authenticateSessionNullAuthentication_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.AUTHENTICATING);

        Identity localIdentity = createIdentity();
        String localInstance = createInstanceName();
        Authentication plainAuthentication = null;

        // Act
        try {
            target.authenticateSession(localIdentity, plainAuthentication, localInstance, listener);
        } catch (IllegalArgumentException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An IllegalArgumentException should be threw");
    }

    //endregion authenticateSession

    //region sendReceivedNotification

    @Test
    public void sendReceivedNotification_EstablishedState_CallsTransport() throws Exception {
        // Arrange
        PlainDocument content = createTextContent();
        Message message = createMessage(content);

        TestClientChannel target = getTarget(SessionState.ESTABLISHED);

        // Act
        target.sendReceivedNotification(message.getId(), message.getFrom());

        // Assert
        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Notification.class);
        Notification sentNotification = (Notification)transport.getSentEnvelopes()[0];
        assertThat(sentNotification.getId()).isEqualTo(message.getId());
        assertThat(sentNotification.getTo()).isEqualTo(message.getFrom());
        assertThat(sentNotification.getEvent()).isEqualTo(Notification.Event.RECEIVED);
    }

    @Test
    public void sendReceivedNotification_NullTo_ThrowsIllegalArgumentException() throws Exception {
        // Arrange
        Message message = createMessage(null);

        TestClientChannel target = getTarget(SessionState.ESTABLISHED);
        // Act
        try {
            target.sendReceivedNotification(message.getId(), null);
        } catch (IllegalArgumentException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            return;
        }

        fail("An IllegalArgumentException should be threw");
    }

    //endregion sendReceivedNotification

    //region sendFinishingSession

    @Test
    public void sendFinishingSessionEstablishedState_CallsTransport() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.ESTABLISHED);

        // Act
        target.sendFinishingSession();

        // Assert
        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Session.class);
        Session sentSession = (Session)transport.getSentEnvelopes()[0];
        assertThat(sentSession.getId()).isEqualTo(target.getSessionId());
        assertThat(sentSession.getState()).isEqualTo(SessionState.FINISHING);
    }

    @Test
    public void sendFinishingSessionNewState_ThrowsIllegalStateException() throws Exception {
        // Arrange
        TestClientChannel target = getTarget(SessionState.NEW);

        // Act
        try {
            target.sendFinishingSession();
        } catch (IllegalStateException e) {
            // Assert
            assertThat(transport.getSentEnvelopes()).isEmpty();
            verify(listener, never()).onReceiveSession(any(Session.class));
            return;
        }

        fail("An IllegalStateException should be threw");
    }

    //endregion sendFinishingSession

    //region sendMessage

    @Test
    public void sendMessage_DelegateMessage_FillsFromTheSession()
    {
        PlainDocument content = createTextContent();
        Message message = createMessage(content);

        Node remoteNode = createNode();
        Node localNode = createNode();

        Node senderNode = createNode();
        Node destinationNode = createNode();

        message.setFrom(senderNode.copy());
        message.setTo(destinationNode.copy());

        TestClientChannel target = getTarget(SessionState.ESTABLISHED, true, remoteNode, localNode, UUID.randomUUID(), false);

        try {
            target.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Message.class);
        Message sentMessage = (Message)transport.getSentEnvelopes()[0];
        assertThat(sentMessage.getId()).isEqualTo(message.getId());
        assertThat(sentMessage.getFrom()).isEqualTo(senderNode);
        assertThat(sentMessage.getTo()).isEqualTo(destinationNode);
        assertThat(sentMessage.getPp()).isNotNull();
        assertThat(sentMessage.getPp()).isEqualTo(localNode);
        assertThat(sentMessage.getContent()).isEqualTo(message.getContent());

    }

    @Test
    public void sendMessage_DelegateMessageWithPpAndEmptyDomain_FillsFromTheSession()
    {
        PlainDocument content = createTextContent();
        Message message = createMessage(content);

        Node remoteNode = createNode();
        Node localNode = createNode();

        Node senderNode = createNode();
        Node destinationNode = createNode();

        message.setFrom(senderNode.copy());
        message.setTo(destinationNode.copy());
        message.setPp(localNode.copy());
        message.getPp().setDomain(null);

        TestClientChannel target = getTarget(SessionState.ESTABLISHED, true, remoteNode, localNode);

        try {
            target.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(transport.getSentEnvelopes()).hasSize(1);
        assertThat(transport.getSentEnvelopes()[0]).isInstanceOf(Message.class);
        Message sentMessage = (Message)transport.getSentEnvelopes()[0];
        assertThat(sentMessage.getId()).isEqualTo(message.getId());
        assertThat(sentMessage.getFrom()).isEqualTo(senderNode);
        assertThat(sentMessage.getTo()).isEqualTo(destinationNode);
        assertThat(sentMessage.getPp()).isNotNull();
        assertThat(sentMessage.getPp()).isEqualTo(localNode);
        assertThat(sentMessage.getContent()).isEqualTo(message.getContent());

    }

    //endregion sendMessage

    //region receiveMessage

    @Test
    public void receiveMessage_MessageReceivedAndAutoNotifyReceiptTrue_SendsNotificationToTransport()
    {
        PlainDocument content = createTextContent();
        Message message = createMessage(content);

        TestClientChannel target = getTarget(SessionState.ESTABLISHED, true);
        target.raiseOnReceiveMessage(message);

        assertThat(transport.getSentEnvelopes()).hasSize(1);
        Notification notification = (Notification) transport.getSentEnvelopes()[0];
        assertThat(notification != null);
        assertThat(notification.getId()).isEqualTo(message.getId());
        assertThat(notification.getTo()).isEqualTo(message.getFrom());
        assertThat(notification.getEvent()).isEqualTo(Notification.Event.RECEIVED);

    }

    @Test
    public void receiveMessageAsync_MessageReceivedAndAutoNotifyReceiptFalse_DoNotSendsNotificationToTransport()
    {
        PlainDocument content = createTextContent();
        Message message = createMessage(content);

        TestClientChannel target = getTarget(SessionState.ESTABLISHED, false);
        target.raiseOnReceiveMessage(message);

        assertThat(transport.getSentEnvelopes()).isEmpty();
    }

    @Test
    public void receiveMessageAsync_FireAndForgetMessageReceivedAndAutoNotifyReceiptTrue_DoNotSendsNotificationToTransport()
    {
        PlainDocument content = createTextContent();
        Message message = createMessage(content);
        message.setId(null);

        TestClientChannel target = getTarget(SessionState.ESTABLISHED, true);
        target.raiseOnReceiveMessage(message);

        assertThat(transport.getSentEnvelopes()).isEmpty();
    }

    // endregion receiveMessage

    @Test
    public void authenticateSession_AuthenticatingStateEstablishedSessionReceived_SetsStateAndNodeProperties()
    {

        PlainAuthentication authentication = createPlainAuthentication();
        Identity identity = createIdentity();

        TestClientChannel target = getTarget(SessionState.AUTHENTICATING, true);

        Session establishedSession = createSession(SessionState.ESTABLISHED);
        establishedSession.setId(target.getSessionId());

        transport.addNextEnvelopeToReturn(establishedSession);

        try {
            target.authenticateSession(identity, authentication, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(target.getState()).isEqualTo(establishedSession.getState());
        assertThat(target.getLocalNode()).isEqualTo(establishedSession.getTo());
        assertThat(target.getRemoteNode()).isEqualTo(establishedSession.getFrom());

    }

    @Test
    public void receiveFinishedSession_EstablishedStateFinishedSessionReceived_SetsStateAndClosesTransport()
    {
        Session session = createSession(SessionState.FINISHED);

        TestClientChannel target = getTarget(SessionState.ESTABLISHED, true);
        target.setSessionListener(listener);

        transport.addNextEnvelopeToReturn(session);

        try {
            target.sendFinishingSession();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(target.getState()).isEqualTo(session.getState());
        assertThat(transport.getSentEnvelopes()).hasSize(1);

        Session sentSession = (Session) transport.getSentEnvelopes()[0];

        assertThat(sentSession.getState()).isEqualTo(SessionState.FINISHING);
        assertThat(transport.isClosed());
    }

    @Test
    public void receiveFinishedSession_EstablishedStateFailedSessionReceived_SetsStateAndClosesTransport()
    {
        Session receivedSession = createSession(SessionState.FAILED);

        TestClientChannel target = getTarget(SessionState.ESTABLISHED, true);
        target.raiseOnReceiveSession(receivedSession);

        assertThat(target.getState()).isEqualTo(receivedSession.getState());
        assertThat(transport.isClosed());
    }

    @Test
    public void authenticateSession_AuthenticatingStateFailedSessionReceived_SetsStateAndClosesTransport()
    {
        Authentication authentication = createPlainAuthentication();
        Identity identity = createIdentity();
        Session receivedSession = createSession(SessionState.FAILED);

        transport.addNextEnvelopeToReturn(receivedSession);

        TestClientChannel target = getTarget(SessionState.AUTHENTICATING, true);

        try {
            target.authenticateSession(identity, authentication, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(target.getState()).isEqualTo(receivedSession.getState());
        assertThat(transport.isClosed());
    }


    private TestClientChannel getTarget() {
        return getTarget(SessionState.NEW);
    }

    private TestClientChannel getTarget(Session.SessionState state) {
        return getTarget(state, false, null, null, UUID.randomUUID(), false);
    }

    private TestClientChannel getTarget(Session.SessionState state, boolean autoNotifyReceipt) {
        return getTarget(state, false, null, null, UUID.randomUUID(), autoNotifyReceipt);
    }

    private TestClientChannel getTarget(SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode) {
        return getTarget(state, fillEnvelopeRecipients, remoteNode, localNode, null, false);
    }

    private TestClientChannel getTarget(Session.SessionState state, boolean fillEnvelopeRecipients, Node remoteNode, Node localNode, UUID sessionId, boolean autoNotifyReceipt) {
        return new TestClientChannel(transport, state, fillEnvelopeRecipients, remoteNode, localNode, sessionId, false, autoNotifyReceipt);
    }
}